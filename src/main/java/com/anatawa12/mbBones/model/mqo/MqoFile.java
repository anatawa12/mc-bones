package com.anatawa12.mbBones.model.mqo;

import com.anatawa12.mbBones.Util;
import com.anatawa12.mbBones.math.Vec2f;
import com.anatawa12.mbBones.math.Vec3f;
import com.anatawa12.mbBones.model.IFileLoader;
import com.anatawa12.mbBones.model.ModelFormatException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MqoFile {
    List<Object> objects = new ArrayList<>();
    List<String> loadXMLs = new ArrayList<>();
    Map<Integer, Bone> bones = new HashMap<>();

    private static class Object {
        final @NotNull String name;
        int uid;
        final @NotNull Vertex @NotNull [] vertices;
        final @NotNull Face @NotNull [] faces;

        // true if bone is enabled.
        boolean bone;

        private Object(@NotNull String name,
                       int uid,
                       @NotNull Vertex @NotNull [] vertices,
                       @NotNull Face @NotNull [] faces) {
            this.name = name;
            this.uid = uid;
            this.vertices = vertices;
            this.faces = faces;
        }
    }

    private static class Vertex {
        final @NotNull Vec3f pos;
        public Bone bone;

        private Vertex(@NotNull Vec3f pos) {
            this.pos = pos;
        }
    }

    private static class Bone {
        final int boneId;
        final int parentId;
        final String boneName;
        final @NotNull Vec3f pos;
        final float head;
        final float pitch;
        final float bank;

        @Nullable Bone parent;
        List<Bone> children = new ArrayList<>();

        private Bone(int boneId, int parentId, String boneName,
                     @NotNull Vec3f pos,
                     float head, float pitch, float bank) {
            this.boneId = boneId;
            this.parentId = parentId;
            this.boneName = boneName;
            this.pos = pos;
            this.head = head;
            this.pitch = pitch;
            this.bank = bank;
        }
    }

    private static class Face {
        final int @NotNull [] vertexIndices;
        final @NotNull Vec2f @Nullable [] uvs;

        public Face(int @NotNull [] vertexIndices, @NotNull Vec2f @Nullable [] uvs) {
            this.vertexIndices = vertexIndices;
            this.uvs = uvs;
        }
    }

    //region reading MQO
    void read(IFileLoader loader, String fileName) throws IOException {
        MqoReader reader = new MqoReader(loader.getStream(fileName));
        readHeader(reader);
        while (true) {
            if (readRootChunk(reader))
                break;
        }
        normalizeObjects();
        for (String loadXML : loadXMLs) {
            readMQX(loader.getStream(fileName, loadXML));
        }
        normalizeBones();
    }

    private void readHeader(MqoReader reader) throws IOException {
        if (!"Metasequoia".equalsIgnoreCase(reader.readToken()))
            throw reader.error("invalid header");
        if (!"Document".equalsIgnoreCase(reader.readToken()))
            throw reader.error("invalid header");
        reader.nextLine();
        if (!"Format".equalsIgnoreCase(reader.readToken()))
            throw reader.error("invalid header");
        String format = reader.readToken();
        if (!"Ver".equalsIgnoreCase(reader.readToken()))
            throw reader.error("invalid header");
        String version = reader.readToken();
        if (!"Text".equalsIgnoreCase(format))
            throw reader.error("unsupported mqo format: " + format);

        if (!version.startsWith("1."))
            throw reader.error("unsupported mqo version: " + version);
        reader.nextLine();
    }

    /**
     * @return true if eof reached
     */
    @SuppressWarnings("SpellCheckingInspection")
    private boolean readRootChunk(MqoReader reader) throws IOException {
        String chunkName = reader.readTokenNullable();
        if (chunkName == null) return true;
        switch (chunkName.toLowerCase(Locale.ROOT)) {
            case "trialnoise":
                throw reader.error("TrialNoise found!");
            case "includexml": {
                loadXMLs.add(reader.readString());
                reader.nextLine();
                break;
            }
            case "object": {
                String name = reader.readString();
                reader.beginBlockChunk();
                objects.add(readObjectChunk(reader, name));
            }
            default: reader.skipChunk();
        }
        return false;
    }

    private Object readObjectChunk(MqoReader reader, String name) throws IOException {
        int uid = -1;
        @NotNull Vertex @Nullable [] vertices = null;
        @NotNull Face @Nullable [] faces = null;
        while (!reader.lookaheadCloseChunk()) {
            switch (reader.readToken().toLowerCase(Locale.ROOT)) {
                case "uid": {
                    uid = reader.readInt();
                    reader.nextLine();
                    break;
                }
                case "vertex": {
                    int vertexCount = reader.readInt();
                    vertices = new Vertex[vertexCount];
                    reader.beginBlockChunk();
                    for (int i = 0; i < vertexCount; i++) {
                        float x = reader.readFloat();
                        float y = reader.readFloat();
                        float z = reader.readFloat();
                        vertices[i] = new Vertex(new Vec3f(x, y, z));
                        reader.nextLine();
                    }
                    reader.closeChunk();
                    break;
                }
                case "bvertex": {
                    int vertexCount = reader.readInt();
                    reader.beginBlockChunk();
                    vertices = readBVertexChunk(reader, vertexCount);
                    break;
                }
                case "face": {
                    int faceCount = reader.readInt();
                    reader.beginBlockChunk();
                    faces = new Face[faceCount];
                    for (int i = 0; i < faceCount; i++) {
                        faces[i] = readFaceLine(reader);
                        reader.nextLine();
                    }
                    reader.closeChunk();
                    break;
                }
                default: reader.skipChunk();
            }
        }
        reader.closeChunk();
        if (vertices == null)
            throw reader.error("Vertex or BVertex chunk not found in Object chunk");
        if (faces == null)
            throw reader.error("Face chunk not found in Object chunk");
        return new Object(
                name,
                uid,
                vertices,
                faces
        );
    }

    private @NotNull Vertex @Nullable [] readBVertexChunk(MqoReader reader, int vertexCountIn) throws IOException {
        @NotNull Vertex @Nullable [] vertices = null;
        while (!reader.lookaheadCloseChunk()) {
            if ("vector".equals(reader.readToken().toLowerCase(Locale.ROOT))) {
                int vertexCount = reader.readInt();
                if (vertexCountIn != vertexCount)
                    throw reader.error("vertex count mismatch between BVertex chunk and Vector chunk");
                reader.readChar('[');
                int dataSize = reader.readInt();
                reader.readChar(']');
                reader.nextLine();
                if (vertexCount * 12 != dataSize)
                    throw reader.error("vertex count and data size mismatch");
                ByteBuffer buffer = ByteBuffer.wrap(reader.readFully(dataSize));
                reader.nextLine();
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                vertices = new Vertex[vertexCount];
                for (int i = 0; i < vertexCount; i++) {
                    float x = buffer.getFloat();
                    float y = buffer.getFloat();
                    float z = buffer.getFloat();
                    vertices[i] = new Vertex(new Vec3f(x, y, z));
                }
            } else {
                reader.skipChunk();
            }
        }
        reader.closeChunk();
        return vertices;
    }

    private @NotNull Face readFaceLine(MqoReader reader) throws IOException {
        int vertexCount = reader.readInt();
        int @Nullable [] vertexIndices = null;
        @NotNull Vec2f @Nullable [] uvs = null;
        String token;
        while ((token = reader.readTokenNullable()) != null) {
            reader.readChar('(');
            switch (token) {
                case "V":
                    vertexIndices = new int[vertexCount];
                    for (int i = 0; i < vertexIndices.length; i++) {
                        vertexIndices[i] = reader.readInt();
                    }
                    break;
                case "UV":
                    uvs = new Vec2f[vertexCount];
                    for (int i = 0; i < uvs.length; i++) {
                        uvs[i] = new Vec2f(reader.readFloat(), reader.readFloat());
                    }
                    break;
                default:
                    reader.skipUntil(')');
            }
            reader.readChar(')');
        }
        if (vertexIndices == null)
            throw reader.error("V() not found in face");
        return new Face(vertexIndices, uvs);
    }
    //endregion

    private void normalizeObjects() {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).uid == -1) objects.get(i).uid = i;
        }
    }


    //region reading MQX

    public ModelFormatException xmlError(String msg) throws ModelFormatException {
        throw new ModelFormatException("in XML: " + msg);
    }

    void readMQX(InputStream is) throws IOException {
        try {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder documentBuilder;
            try {
                documentBuilder = documentBuilderFactory
                        .newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new AssertionError(e);
            }
            final Document document;
            try {
                document = documentBuilder.parse(is);
            } catch (SAXException e) {
                throw new ModelFormatException("Failred to parse XML", e);
            }
            Node root = document.getChildNodes().item(0);
            if (!root.getNodeName().equals("MetasequoiaDocument"))
                throw new ModelFormatException("invalid root node");
            for (Node node : Util.iterator(root.getChildNodes())) {
                if (node.getNodeName().equals("Plugin.56A31D20.71F282AB")) {
                    readBonePluginXML(node);
                }
            }
        } catch (NullPointerException | NumberFormatException npe) {
            throw new ModelFormatException("Invalid or unsupported MQX format", npe);
        }
    }

    private void readBonePluginXML(Node pluginXML) throws IOException {
        for (Node node : Util.iterator(pluginXML.getChildNodes())) {
            switch (node.getNodeName()) {
                case "BoneSet2":
                    for (Node bone : Util.iterator(node.getChildNodes())) {
                        if (bone.getNodeName().equals("Bone"))
                            readBoneXML(bone);
                    }
                    break;
                case "Obj":
                    int objId = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
                    Object obj = objects.stream().filter(it -> it.uid == objId).findFirst().orElse(null);
                    if (obj == null) throw xmlError("obj#" + objId + "not found");
                    obj.bone = true;
                    break;
            }
        }
    }

    private void readBoneXML(Node boneNode) throws IOException {
        int boneId = Integer.parseInt(boneNode.getAttributes().getNamedItem("id").getNodeValue());
        Node parentIdNode = boneNode.getAttributes().getNamedItem("parent");
        int parentId = parentIdNode == null ? 0 : Integer.parseInt(parentIdNode.getNodeValue());
        String boneName = boneNode.getAttributes().getNamedItem("name").getNodeValue();
        Vec3f pos = parse3f(boneNode.getAttributes().getNamedItem("pos").getNodeValue());
        Node upVectorNode = boneNode.getAttributes().getNamedItem("upVector");
        Vec3f hpb = parse3f(upVectorNode == null ? null : upVectorNode.getNodeValue());
        Bone bone = new Bone(
                boneId, parentId, boneName,
                pos,
                hpb.x, hpb.y, hpb.z);
        bones.put(boneId, bone);

        for (Node node : Util.iterator(boneNode.getChildNodes())) {
            if (!"W".equals(node.getNodeName())) continue;
            int objId = Integer.parseInt(node.getAttributes().getNamedItem("obj").getNodeValue());
            Object obj = objects.stream().filter(it -> it.uid == objId).findFirst().orElse(null);
            if (obj == null) throw xmlError("obj#" + objId + "not found");
            for (Node vertexNode : Util.iterator(node.getChildNodes())) {
                if (!"V".equals(vertexNode.getNodeName())) continue;
                int vertexId = Integer.parseInt(vertexNode.getAttributes().getNamedItem("v").getNodeValue());
                Vertex vertex = obj.vertices[vertexId - 1];
                if (vertex.bone != null)
                    throw xmlError("multiple bone wight found at vertex #" + vertexId);
                vertex.bone = bone;
            }
        }
    }

    private Vec3f parse3f(String pos) {
        if (pos == null) return Vec3f.ORIGIN;
        String[] elements = pos.split(",", 3);
        return new Vec3f(
                Float.parseFloat(elements[0]),
                Float.parseFloat(elements[1]),
                Float.parseFloat(elements[2])
        );
    }

    //endregion

    private void normalizeBones() throws ModelFormatException {
        for (Bone bone : bones.values()) {
            if (bone.parentId != 0) {
                bone.parent = bones.get(bone.parentId);
                if (bone.parent == null)
                    throw xmlError("Unknown parent bone for #" + bone.boneId + ": #" + bone.parentId);
                bone.parent.children.add(bone);
            }
        }
    }
}
