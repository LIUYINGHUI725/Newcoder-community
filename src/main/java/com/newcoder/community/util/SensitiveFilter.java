package com.newcoder.community.util;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.text.normalizer.Trie;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    //根结点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //得到缓冲流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //添加前缀树
                this.addKeyword(keyword);

            }

        } catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }

    }

    //将一个敏感词添加到前缀树当中
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                //初始化子结点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            //指针tempNode指向子结点，进入下一轮循环
            tempNode = subNode;

            //设置结束标识 表示这是一个敏感词
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }

        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        //指针1 指着树的
        TrieNode tempNode = rootNode;
        //指针2 2和3是指向待检查字符串的
        int begin = 0;
        //指针3
        int position = 0;
        //结果字符串
        StringBuilder sb = new StringBuilder();

        while (begin < text.length()) {

            if (position < text.length()) {
                char c = text.charAt(position);
                //跳过符号 避免用户刻意干扰
                if (isSymbol(c)) {
                    //若指针1处于根结点 将此符号计入结果 让指针2向下走一步
                    if (tempNode == rootNode) {
                        sb.append(c);
                        begin++;
                    }
                    //无论符号在开头或中间 指针3都向下走一步
                    position++;
                    continue;
                }

                //检查下级结点
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    //以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    //进入下一个位置
                    position = ++begin;
                    //指针1重新指向根结点 开始下一轮判断
                    tempNode = rootNode;
                } else if (tempNode.isKeywordEnd()) {
                    //发现了敏感词 需要将begin到position这段字符串替换
                    sb.append(REPLACEMENT);
                    //进入下一个位置
                    begin = ++position;
                    tempNode = rootNode;
                } else {
                    //继续检查下一个字符
                    position++;
                }

            }
            else{
                // position 遍历越界仍未匹配到敏感词
                sb.append(text.charAt(begin));
                position=++begin;
                tempNode=rootNode;
            }
        }
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c) {
        //0x2E80 ~ 0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //定义了前缀树的数据结构
    private class TrieNode {

        //关键词结束标识
        private boolean isKeywordEnd = false;
        //子结点（key是下级字符，value是下级结点）
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子结点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子结点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }
}

