package chc.nlputil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by fangy on 13-12-13.
 * 分割和输出记�?
 */
public class Tokenizer {

//    private String[] tokens;
//    int currentIndex;
    private List<String> tokens;
    private ListIterator<String> tokenIter;

    public Tokenizer(){
        tokens = new LinkedList<String>();
        tokenIter = tokens.listIterator();
    }

    /**
     * 分割�?段文本，得到�?列记�?
     * @param text 文本
     * @param delim 分割�?
     */
    public Tokenizer(String text, String delim){

        tokens = Arrays.asList(text.split(delim));
        tokenIter = tokens.listIterator();
    }

    /**
     * 获得记号的数�?
     * @return 数量
     */
    public int size(){
        return tokens.size();
    }

    /**
     * 遍历记号时，查询是否还有记号未遍�?
     * @return 若还有记号未遍历，则返回true，否则返回false�?
     */
    public boolean hasMoreTokens(){
        return tokenIter.hasNext();
    }

    /**
     * 遍历记号时获得下�?个之前未遍历的记�?
     * @return 记号
     */
    public String nextToken(){

        return tokenIter.next();
    }

    /**
     * 向原有记号序列的末尾添加�?个记�?
     * @param token 待添加的记号
     */
    public void add(String token){

        if (token == null){
            return;
        }

        tokens.add(token);
    }

    /**
     * 以分割符连接记号并输�?
     * @param delim 分割�?
     * @return 记号由分割符连接的字符串
     */
    public String toString(String delim){
        StringBuilder sb = new StringBuilder();
        if (tokens.size() < 1){
            return sb.toString();
        }
        ListIterator<String> tempTokenIter = tokens.listIterator();
        sb.append(tempTokenIter.next());
        while (tempTokenIter.hasNext()){
            sb.append(" ").append(tempTokenIter.next());
        }

        return sb.toString();
    }


}
