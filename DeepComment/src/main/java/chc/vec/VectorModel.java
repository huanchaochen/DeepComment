package chc.vec;

import java.io.*;
import java.util.*;

/**
 * User: fangy
 * Date: 13-12-9
 * Time: 下午2:30
 */
public class VectorModel {

    private Map<String, float[]> wordMap = new HashMap<String, float[]>();
    private int vectorSize = 200; //特征�?

    private int topNSize = 40;

    public Map<String, float[]> getWordMap() {
        return wordMap;
    }

    public void setWordMap(Map<String, float[]> wordMap){
        this.wordMap = wordMap;
    }

    /**
     * 获取�?相似词的数量
     * @return �?相似词的数量
     */
    public int getTopNSize() {
        return topNSize;
    }

    /**
     * 设置�?相似词的数量
     * @param topNSize 数量
     */
    public void setTopNSize(int topNSize) {
        this.topNSize = topNSize;
    }

    public int getVectorSize() {
        return vectorSize;
    }

    public void setVectorSize(int vectorSize) {
        this.vectorSize = vectorSize;
    }

    /**
     * 私有构�?�函�?
     * @param wordMap 词向量哈希表
     * @param vectorSize 词向量长�?
     */
    public VectorModel(Map<String, float[]> wordMap, int vectorSize){

        if (wordMap == null || wordMap.isEmpty()){
            throw new IllegalArgumentException("word2vec的词向量为空，请先训练模型�??");
        }
        if (vectorSize <= 0){
            throw new IllegalArgumentException("词向量长度（layerSize）应大于0");
        }

        this.wordMap = wordMap;
        this.vectorSize = vectorSize;
    }


    /**
     * 使用Word2Vec保存的模型加载词向量模型
     * @param path 模型文件路径
     * @return 词向量模�?
     */
    public static VectorModel loadFromFile(String path){

        if (path == null || path.isEmpty()){
            throw new IllegalArgumentException("模型路径可以为null或空�?");
        }

        DataInputStream dis = null;
        int wordCount, layerSizeLoaded = 0;
        Map<String, float[]> wordMapLoaded = new HashMap<String, float[]>();
        try {
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
            wordCount = dis.readInt();
            layerSizeLoaded = dis.readInt();
            float vector;

            String key;
            float[] value;
            for (int i = 0; i < wordCount; i++) {
                key = dis.readUTF();
                value = new float[layerSizeLoaded];
                double len = 0;
                for (int j = 0; j < layerSizeLoaded; j++) {
                    vector = dis.readFloat();
                    len += vector * vector;
                    value[j] = vector;
                }

                len = Math.sqrt(len);

                for (int j = 0; j < layerSizeLoaded; j++) {
                    value[j] /= len;
                }
                wordMapLoaded.put(key, value);
            }

        } catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            try {
                if (dis != null){
                    dis.close();
                }
            } catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

        return new VectorModel(wordMapLoaded, layerSizeLoaded);

    }

    /**
     * 保存词向量模�?
     * @param file 模型存放路径
     */
    public void saveModel(File file) {

        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(file)));
            dataOutputStream.writeInt(wordMap.size());
            dataOutputStream.writeInt(vectorSize);
            for (Map.Entry<String, float[]> element : wordMap.entrySet()) {
                dataOutputStream.writeUTF(element.getKey());
                for (float d : element.getValue()) {
                    dataOutputStream.writeFloat(d);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null){
                    dataOutputStream.close();
                }
            }catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * 获取与词word�?相近topNSize个词
     * @param queryWord �?
     * @return 相近词集，若模型不包含词word，则返回空集
     */
    public Set<WordScore> similar(String queryWord){

        float[] center = wordMap.get(queryWord);
        if (center == null){
            return Collections.emptySet();
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize + 1;
        TreeSet<WordScore> result = new TreeSet<WordScore>();
        for (int i = 0; i < resultSize; i++){
            result.add(new WordScore("^_^", -Float.MAX_VALUE));
        }
        float minDist = -Float.MAX_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()){
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++){
                dist += center[i] * vector[i];
            }
            if (dist > minDist){
                result.add(new WordScore(entry.getKey(), dist));
                minDist = result.pollLast().score;
            }
        }
        result.pollFirst();

        return result;
    }

    public Set<WordScore> similar(float[] center){
        if (center == null || center.length != vectorSize){
            return Collections.emptySet();
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
        TreeSet<WordScore> result = new TreeSet<WordScore>();
        for (int i = 0; i < resultSize; i++){
            result.add(new WordScore("^_^", -Float.MAX_VALUE));
        }
        float minDist = -Float.MAX_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()){
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++){
                dist += center[i] * vector[i];
            }
            if (dist > minDist){
                result.add(new WordScore(entry.getKey(), dist));
                minDist = result.pollLast().score;
            }
        }
//        result.pollFirst();

        return result;
    }

    /**
     * 词迁移，即word1 - word0 + word2 的结果，若三个词中有�?个不在模型中�?
     * 也就是没有词向量，则返回空集
     * @param word0 �?
     * @param word1 �?
     * @param word2 �?
     * @return 与结果最相近的前topNSize个词
     */
    public TreeSet<WordScore> analogy(String word0, String word1, String word2) {
        float[] wv0 = wordMap.get(word0);
        float[] wv1 = wordMap.get(word1);
        float[] wv2 = wordMap.get(word2);

        if (wv1 == null || wv2 == null || wv0 == null) {
            return null;
        }
        float[] center = new float[vectorSize];
        for (int i = 0; i < vectorSize; i++) {
            center[i] = wv1[i] - wv0[i] + wv2[i];
        }

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
        TreeSet<WordScore> result = new TreeSet<WordScore>();
        for (int i = 0; i < resultSize; i++){
            result.add(new WordScore("^_^", -Float.MAX_VALUE));
        }
        String name;
        float minDist = -Float.MAX_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()){
            name = entry.getKey();
            if (name.equals(word1) || name.equals((word2))){
                continue;
            }
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++){
                dist += center[i] * vector[i];
            }
            if (dist > minDist){
                result.add(new WordScore(entry.getKey(), dist));
                minDist = result.pollLast().score;
            }
        }
        return result;
    }

    public float[] getWordVector(String word) {
        return wordMap.get(word);
    }
    


    public class WordScore implements Comparable<WordScore> {

        public String name;
        public float score;

        public WordScore(String name, float score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public String toString() {
            return this.name + "\t" + score;
        }

        public int compareTo(WordScore o) {
            if (this.score < o.score) {
                return 1;
            } else {
                return -1;
            }
        }
    }

}
