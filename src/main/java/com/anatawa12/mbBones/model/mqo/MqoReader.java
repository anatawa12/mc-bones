package com.anatawa12.mbBones.model.mqo;

import com.anatawa12.mbBones.model.ModelFormatException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

class MqoReader {
    private final InputStream reader;
    private int buf = -1;

    public MqoReader(InputStream reader) {
        this.reader = reader;
    }

    public @NotNull String readToken() throws IOException {
        return readToken("Token");
    }

    private @NotNull String readToken(String tokenName) throws IOException {
        String token = readTokenNullable();
        if (token == null)
            if (buf == -1)
                throw error("Expected " + tokenName + " but was EOF");
            else
                throw error("Expected " + tokenName + " but was NewLine");
        return token;
    }

    public @Nullable String readTokenNullable() throws IOException {
        int c;
        skipWS:
        while (true) switch (c = read()) {
            case -1:
                return null;
            case ' ':
            case '\t':
                break;
            case '\r':
            case '\n':
                buf = c;
                return null;
            default:
                break skipWS;
        }
        StringBuilder builder = new StringBuilder();
        builder.append((char) c);
        while (true) switch (c = read()) {
            case -1:
            case ' ':
            case '\t':
            case '\r':
            case '\n':
            case '(':
            case ')':
            case '[':
            case ']':
                buf = c;
                return builder.toString();
            default:
                builder.append((char) c);
                break;
        }
    }

    public @NotNull String readString() throws IOException {
        readChar('"');
        StringBuilder builder = new StringBuilder();
        int c;
        while (true) switch (c = read()) {
            case -1:
                throw error("Unexpected EOF");
            case '"':
                return builder.toString();
            default:
                builder.append((char) c);
                break;
        }
    }

    public void nextLine() throws IOException {
        int c;
        while (true) switch (c = read()) {
            case -1:
            case '\r':
            case '\n':
                do {
                    buf = -1;
                    buf = read();
                } while (buf == '\n' || buf == '\r');
                return;
            case ' ':
                break;
            default:
                throw error("Expected NewLine but was '" + (char)c + "'");
        }
    }

    public boolean lookaheadChar(char target) throws IOException {
        int c;
        while (true) switch (c = read()) {
            case -1:
                return false;
            case ' ':
            case '\t':
                break;
            default:
                buf = c;
                return c == target;
        }
    }

    public void readChar(char target) throws IOException {
        if (lookaheadChar(target)) {
            buf = -1;
            return;
        }
        if (buf == -1)
            throw error("Expected '" + target + "' but was EOF");
        else
            throw error("Expected '" + target + "' but was '" + (char)buf + "'");
    }

    public void beginBlockChunk() throws IOException {
        readChar('{');
        nextLine();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean lookaheadCloseChunk() throws IOException {
        return lookaheadChar('}');
    }

    public void closeChunk() throws IOException {
        readChar('}');
        nextLine();
    }

    public int readInt() throws IOException {
        try {
            return Integer.parseInt(readToken("Integer"));
        } catch (NumberFormatException e) {
            throw error("Expected Integer but was token");
        }
    }

    public float readFloat() throws IOException {
        try {
            return Float.parseFloat(readToken("Float"));
        } catch (NumberFormatException e) {
            throw error("Expected Integer but was token");
        }
    }

    public byte[] readFully(int size) throws IOException {
        if (size == 0) return new byte[0];
        byte[] ary = new byte[size];
        if (buf != -1) {
            ary[0] = (byte) buf;
            buf = -1;
            if (reader.read(ary, 0, size - 1) != size -1)
                throw error("Unexpected EOF");
        } else {
            if (reader.read(ary) != size)
                throw error("Unexpected EOF");
        }
        return ary;
    }

    public void skipChunk() throws IOException {
        // 0: start
        // 1: after {
        // 2: after }
        int stat = 0;
        int chunkNestCount = 0;
        while (true) switch (read()) {
            case -1:
            case '\r':
            case '\n':
                switch (stat) {
                    case 1:
                        chunkNestCount++;
                        break;
                    case 2:
                        chunkNestCount--;
                        break;
                }
                stat = 0;
                do {
                    buf = -1;
                    buf = read();
                } while (buf == '\n' || buf == '\r');
                if (chunkNestCount == 0) return;
                break;
            case '{':
                stat = 1;
                break;
            case '}':
                stat = 2;
                break;
            default:
                stat = 0;
                break;
        }
    }

    public void skipUntil(char target) throws IOException {
        int c;
        while (true) switch (c = read()) {
            case '\r':
            case '\n':
                throw error("Expected '" + target + "' but was NewLine");
            case -1:
            default:
                if (c == target) {
                    buf = c;
                    return;
                }
        }
    }

    private int line = 1;
    // 0: normal state
    // 1: after CR(\r), should not increment line on LF(\n)
    private int stat = 0;

    private int read() throws IOException {
        if (buf != -1) {
            int ret = buf;
            buf = -1;
            return ret;
        }
        int ret = reader.read();
        if (ret == '\r') {
            line++;
            stat = 1;
        } else if (ret == '\n') {
            if (stat != 1) line++;
            stat = 0;
        } else {
            stat = 0;
        }
        return ret;
    }

    public ModelFormatException error(String msg) throws ModelFormatException {
        throw new ModelFormatException("line# " + line + ": " + msg);
    }
}
