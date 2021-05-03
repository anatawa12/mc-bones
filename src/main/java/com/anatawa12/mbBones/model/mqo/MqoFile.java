package com.anatawa12.mbBones.model.mqo;

import com.anatawa12.mbBones.BoneTree;
import com.anatawa12.mbBones.BonedMultiObjectModel;
import com.anatawa12.mbBones.BonedObject;
import com.anatawa12.mbBones.Util;
import com.anatawa12.mbBones.math.Quot;
import com.anatawa12.mbBones.math.Vec2f;
import com.anatawa12.mbBones.math.Vec3f;
import com.anatawa12.mbBones.math.Vec4f;
import com.anatawa12.mbBones.model.IFileLoader;
import com.anatawa12.mbBones.model.ModelFormatException;
import com.anatawa12.mbBones.util.Pair;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.anatawa12.mbBones.math.Quot.fromOiler;
import static com.anatawa12.mbBones.math.Quot.rotationRad;
import static com.anatawa12.mbBones.math.Quot.times;
import static com.anatawa12.mbBones.math.Vec3Util.cross;
import static com.anatawa12.mbBones.math.Vec3Util.dot;

public class MqoFile {
    List<Object> objects = new ArrayList<>();
    List<String> loadXMLs = new ArrayList<>();
    Map<Integer, Bone> bones = new HashMap<>();

    public BoneTree buildTree() {
        Map<Integer, Integer> boneIdMapping = new HashMap<>();
        BoneTree.Builder builder = BoneTree.builder(0);
        boneIdMapping.put(null, 0);
        bones.values().stream()
                .filter(it -> it.parentId == 0)
                .forEach(bone -> buildSingleBone(bone, builder, boneIdMapping));
        return builder.build();
    }

    private static final Vec3f up = new Vec3f(0, 1, 0);
    private static void buildSingleBone(Bone bone, BoneTree.Builder parent, Map<Integer, Integer> boneIdMapping) {
        BoneTree.Builder builder = BoneTree.builder(boneIdMapping.size());
        boneIdMapping.put(bone.boneId, builder.id);
        builder.setExternalId(bone.boneId);
        builder.setParent(parent);
        Vec4f rot = Quot.ORIGIN;
        if (bone.children.size() == 1) {
            rot = times(rot, buildRot(bone.children.get(0).pos.sub(bone.pos)));
        } else if (bone.parent != null) {
            rot = times(rot, buildRot(bone.pos.sub(bone.parent.pos)));
        }
        if (bone.head != 0 || bone.pitch != 0 || bone.bank != 0)
            rot = times(rot, fromOiler(bone.head, bone.pitch, bone.bank));
        builder.setRot(rot);
        builder.setPos(bone.pos.div(100));
        for (Bone child : bone.children) {
            buildSingleBone(child, builder, boneIdMapping);
        }
    }

    private static Vec4f buildRot(Vec3f relative) {
        Vec3f rotAxis = cross(up, relative);
        float rot = (float) Math.acos(dot(up, relative) / relative.norm() /* / up.norm() == 1*/);
        return rotationRad(rotAxis, rot);
    }

    public BonedMultiObjectModel buildModel(BoneTree tree) {
        BonedMultiObjectModel.Builder model = BonedMultiObjectModel.builder(tree);
        for (Object object : objects) {
            model.addObject(object.buildObject(tree));
        }
        return model.build();
    }

    private static class Object {
        final @NotNull String name;
        int uid;
        final boolean glowShading;
        final @NotNull Vertex @NotNull [] vertices;
        final @NotNull Face @NotNull [] faces;

        // true if bone is enabled.
        boolean bone;

        private Object(@NotNull String name,
                       int uid,
                       boolean glowShading,
                       @NotNull Vertex @NotNull [] vertices,
                       @NotNull Face @NotNull [] faces) {
            this.name = name;
            this.uid = uid;
            this.glowShading = glowShading;
            this.vertices = vertices;
            this.faces = faces;
        }

        BonedObject buildObject(BoneTree tree) {
            BonedObject.Builder builder = BonedObject.builder(tree);
            builder.setName(name);
            for (Face face : faces) {
                buildFace(builder, tree, face);
            }
            return builder.build();
        }

        private void buildFace(BonedObject.Builder builder, BoneTree tree, Face face) {
            if (face.vertexIndices.length < 3) return;
            BonedObject.Builder.Point p0 = addPoint(builder, tree, face, 0);
            BonedObject.Builder.Point p1 = addPoint(builder, tree, face, 1);
            for (int i = 2; i < face.vertexIndices.length; i++) {
                BonedObject.Builder.Point p2 = addPoint(builder, tree, face, i);
                builder.addTriangle(p0, p2, p1);
                p1 = p2;
            }
        }

        private BonedObject.Builder.Point addPoint(
                @NotNull BonedObject.Builder builder,
                @NotNull BoneTree tree,
                @NotNull Face face,
                int index
        ) {
            Vertex vertex = face.vertices[index];
            Vec2f uv = face.uvs == null ? Vec2f.ORIGIN : face.uvs[index];
            BoneTree.Bone bone = !this.bone || vertex.bone == null ? tree.getRoot()
                    : tree.getByExternalId(vertex.bone.boneId);
            return builder.addPoint(
                    bone,
                    vertex.pos.div(100),
                    face.normals[index],
                    uv);
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
        @NotNull Vertex [] vertices;
        @NotNull Vec3f [] normals;
        final @NotNull Vec2f @Nullable [] uvs;
        public Vec3f normal;

        public Face(int @NotNull [] vertexIndices, @NotNull Vec2f @Nullable [] uvs) {
            this.vertexIndices = vertexIndices;
            this.uvs = uvs;
        }
    }

    //region reading MQO
    public void read(IFileLoader loader, String fileName) throws IOException {
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
        boolean glowShading = false;
        @NotNull Vertex @Nullable [] vertices = null;
        @NotNull Face @Nullable [] faces = null;
        while (!reader.lookaheadCloseChunk()) {
            switch (reader.readToken().toLowerCase(Locale.ROOT)) {
                case "uid": {
                    uid = reader.readInt();
                    reader.nextLine();
                    break;
                }
                case "shading": {
                    glowShading = reader.readInt() == 1;
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
                glowShading,
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
            normalizeObject(objects.get(i));
        }
    }

    private void normalizeObject(Object object) {
        for (Face face : object.faces) {
            face.vertices = new Vertex[face.vertexIndices.length];
            for (int i = 0; i < face.vertexIndices.length; i++) {
                face.vertices[i] = object.vertices[face.vertexIndices[i]];
            }
        }

        // compute normal vector
        for (Face face : object.faces) {
            Vec3f normal;
            switch (face.vertices.length) {
                case 1:
                case 2:
                    continue;
                case 3: {
                    Vec3f p0 = face.vertices[0].pos;
                    Vec3f p1 = face.vertices[1].pos;
                    Vec3f p2 = face.vertices[2].pos;
                    normal = cross(p0.sub(p1), p0.sub(p2));
                    break;
                }
                case 4: {
                    Vec3f p0 = face.vertices[0].pos;
                    Vec3f p1 = face.vertices[1].pos;
                    Vec3f p2 = face.vertices[2].pos;
                    Vec3f p3 = face.vertices[3].pos;

                    normal = cross(p1.sub(p3), p0.sub(p2)).times(2);
                    break;
                }
                default: {
                    normal = Vec3f.ORIGIN;
                    Vec3f p0 = face.vertices[0].pos;
                    Vec3f p1 = face.vertices[1].pos;
                    for (int i = 2; i < face.vertices.length; i++) {
                        Vec3f p2 = face.vertices[i].pos;
                        normal = normal.add(cross(p0.sub(p1), p0.sub(p2)));
                        p1 = p2;
                    }
                    break;
                }
            }
            face.normal = normal;
        }

        // for glow shading
        if (object.glowShading) {
            computeNormalVectorsForGlowShading(object);
        } else {
            computeNormalVectors(object);
        }
    }

    private void computeNormalVectorsForGlowShading(Object object) {
        Map<Vertex, List<Pair<Integer, Face>>> vertexListMap = new HashMap<>();
        for (Vertex vertex : object.vertices) {
            vertexListMap.put(vertex, new ArrayList<>());
        }
        for (Face face : object.faces) {
            for (int i = 0; i < face.vertices.length; i++) {
                vertexListMap.get(face.vertices[i]).add(new Pair<>(i, face));
            }
            face.normals = new Vec3f[face.vertices.length];
        }

        for (List<Pair<Integer, Face>> faces : vertexListMap.values()) {
            Vec3f normal = Vec3f.ORIGIN;
            for (Pair<Integer, Face> face : faces) {
                normal = normal.add(face.b.normal);
            }
            for (Pair<Integer, Face> face : faces) {
                if (normal.norm() < 0.0001)
                    face.b.normals[face.a] = face.b.normal;
                else
                    face.b.normals[face.a] = normal;
            }
        }
    }

    private void computeNormalVectors(Object object) {
        for (Face face : object.faces) {
            face.normals = new Vec3f[face.vertices.length];
            Arrays.fill(face.normals, face.normal);
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
