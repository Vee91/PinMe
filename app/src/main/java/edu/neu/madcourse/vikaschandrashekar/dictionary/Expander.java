package edu.neu.madcourse.vikaschandrashekar.dictionary;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.BitSet;

/**
 * Created by cvikas on 2/2/2017.
 */

public final class Expander {

    private static class Node {
        char ch;
        int frequency;
        Node left;
        Node right;

        Node(char ch, int frequency, Node left, Node right) {
            this.ch = ch;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }
    }

    private static class IntObject {
        int bitPosition;
    }

    public static String expand(Context context, char startChar) throws ClassNotFoundException, IOException {
        long s = System.currentTimeMillis();
        final Node root = deserializeTree(context, startChar);
        long d = System.currentTimeMillis();
        Log.d("time", String.valueOf(d-s));
        return decodeMessage(root, context, startChar);
    }

    private static Node deserializeTree(Context context, char startChar) throws IOException, ClassNotFoundException {
        try {
            InputStream treeIns = context.getResources().openRawResource(
                    context.getResources().getIdentifier("tree_structure_"+startChar,
                            "raw", context.getPackageName()));
            ObjectInputStream inStream = new ObjectInputStream(treeIns);
            try {
                InputStream charIns = context.getResources().openRawResource(
                        context.getResources().getIdentifier("characters_"+startChar,
                                "raw", context.getPackageName()));
                ObjectInputStream inChar = new ObjectInputStream(charIns);
                final BitSet bitSet = (BitSet) inStream.readObject();
                return preOrder(bitSet, inChar, new IntObject());
            } catch (FileNotFoundException f){
                throw f;
            }
        } catch (IOException i){
            throw i;
        }
    }

    private static Node preOrder(BitSet bitSet, ObjectInputStream inChar, IntObject o) throws IOException {
        final Node node = new Node('\0', 0, null, null);

        if (!bitSet.get(o.bitPosition)) {
            o.bitPosition++;
            node.ch = inChar.readChar();
            return node;
        }

        o.bitPosition = o.bitPosition + 1;
        node.left = preOrder(bitSet, inChar, o);

        o.bitPosition = o.bitPosition + 1;
        node.right = preOrder(bitSet, inChar, o);

        return node;
    }

    private static String decodeMessage(Node node, Context context, char startChar) throws IOException, ClassNotFoundException {
        long s = System.currentTimeMillis();
        try {
            InputStream dictionaryIns = context.getResources().openRawResource(
                    context.getResources().getIdentifier("encoded_dictionary_"+startChar,
                            "raw", context.getPackageName()));
            ObjectInputStream inStream = new ObjectInputStream(dictionaryIns);
            final BitSet bitSet = (BitSet) inStream.readObject();
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < (bitSet.length() - 1);) {
                Node temp = node;
                while (temp.left != null) {
                    if (!bitSet.get(i)) {
                        temp = temp.left;
                    } else {
                        temp = temp.right;
                    }
                    i = i + 1;
                }
                stringBuilder.append(temp.ch);
            }
            long d = System.currentTimeMillis();
            Log.d("Time", String.valueOf(d-s));
            return stringBuilder.toString();
        } catch (IOException i){
            throw i;
        }
    }

}
