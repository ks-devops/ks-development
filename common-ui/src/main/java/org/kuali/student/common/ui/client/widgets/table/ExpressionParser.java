package org.kuali.student.common.ui.client.widgets.table;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;


public class ExpressionParser {
    private List<String> errorMessageList = new ArrayList<String>();
    
    public ExpressionParser() {
    }
    public boolean hasError(){
        return errorMessageList.size() > 0;
    }
    public List<String> getErrorMessage(){
        return errorMessageList;
    }
    public Node<Token> parse(String expression) {
        errorMessageList = new ArrayList();
        List<String> tokenValueList = getTokenValue(expression);
        List<Token> tokenList = getTokenList(tokenValueList);
        errorCheck(tokenList);
        if(hasError()){
            return null;
        }
        List<Node<Token>> nodeList = toNodeList(tokenList);
        List<Node<Token>> rpnList = getRPN(nodeList);

        Node<Token> root = binaryTreeFromRPN(rpnList);
        Node<Token> ruleRoot = mergeBinaryTree(root);
        ruleRoot = rebuildTree(root);
        ruleRoot = requence(ruleRoot, tokenList );
        return ruleRoot;
    }
    private Node<Token> requence(Node<Token> binaryTree,List<Token> tokenList ) {
         List<Node> list = binaryTree.getLeafChildren();
         if(list.size() > 1){
             sequeceLeaves(list, tokenList);
             binaryTree.children().removeAll(list);
             for(Node n: list){
                 binaryTree.addNode(n);
             }
         }
         for(Node n: binaryTree.children()){
             if(n.isLeaf() == false){
                 requence(n,tokenList );
             }
         }
         
         
        return binaryTree;
    }
    
    private void sequeceLeaves(List<Node> leafChildList, List<Token> list){
        if(leafChildList.size() == 2){
            if (indexInInputTokenList((Token)leafChildList.get(0).getUserObject(), list)> 
            indexInInputTokenList((Token)leafChildList.get(1).getUserObject(), list)) {
              // swap them
                Token buffer = (Token)leafChildList.get(0).getUserObject();
                leafChildList.get(0).setUserObject(leafChildList.get(1).getUserObject());
                leafChildList.get(1).setUserObject(buffer);
            }
            
        }
        
        for (int out = leafChildList.size() - 1; out > 1; out--){
          for (int in = 0; in < out; in++){
            // inner loop (forward)
            if (indexInInputTokenList((Token)leafChildList.get(in).getUserObject(), list)> 
            indexInInputTokenList((Token)leafChildList.get(in + 1).getUserObject(), list)) {
              // swap them
                Token buffer = (Token)leafChildList.get(in).getUserObject();
                leafChildList.get(in).setUserObject(leafChildList.get(in + 1).getUserObject());
                leafChildList.get(in + 1).setUserObject(buffer);
            }
          }
        }
        //System.out.println(leafChildList);
    }
    private int indexInInputTokenList(Token token, List<Token> list){
        int i = -1;
        for(Token n: list){
            if(n.value != null && token.value != null && n.value.equals(token.value)){
                return i;
            }
            i++;
        }
        return i;
    }
    private Node<Token> rebuildTree(Node<Token> binaryTree) {
        Node root = new Node();
        root.setUserObject(binaryTree.getUserObject());
        List<Node> childList = binaryTree.children();

        ListIterator<Node> itr = childList.listIterator();
        for (; itr.hasNext();) {
            Node node = getDeeperNode(childList);
            if (node.isLeaf()) {
                root.addNode(node);
            } else {
                root.addNode(rebuildTree(node));
            }
            childList.remove(node);
        }

        return root;
    }

    private Node getDeeperNode(List<Node> nodeList) {
        int childCount = 0;
        for (Node node : nodeList) {
            if (childCount < node.getAllChildren().size()) {
                childCount = node.getAllChildren().size();
            }
        }
        for (Node node : nodeList) {
            if (childCount == node.getAllChildren().size()) {
                return node;
            }
        }

        return null;
    }

    private Node<Token> mergeBinaryTree(Node<Token> binaryTree) {
        while (parentEqualsGrandParent(binaryTree)) {
            List<Node> list = binaryTree.getAllChildren();

            for (Node node : list) {
                if (node.getParent() != null && node.getParent().getParent() != null) {
                    Node parentNode = node.getParent();
                    Node grandParentNode = node.getParent().getParent();
                    Token parentToken = (Token) parentNode.getUserObject();
                    Token grandParentToken = (Token) grandParentNode.getUserObject();

                    if (parentToken.type == grandParentToken.type) {
                        for (int i = 0; i < parentNode.getChildCount(); i++) {
                            Node n = parentNode.getChildAt(i);
                            grandParentNode.addNode(n);
                        }
                        grandParentNode.remove(parentNode);
                    }
                }
            }
        }
        return binaryTree;
    }

    private boolean parentEqualsGrandParent(Node<Token> binaryTree) {
        List<Node> list = binaryTree.getAllChildren();

        for (Node node : list) {
            if (node.getParent() != null && node.getParent().getParent() != null) {

                Token parentToken = (Token) node.getParent().getUserObject();
                Token grandParentToken = (Token) node.getParent().getParent().getUserObject();
                if (parentToken.type == grandParentToken.type) {
                    return true;
                }
            }
        }

        return false;
    }

    private Node<Token> binaryTreeFromRPN(List<Node<Token>> rpnList) {
        Stack<Node<Token>> conditionStack = new Stack<Node<Token>>();
        for (Node<Token> node : rpnList) {
            if (node.getUserObject().type == Token.Condition) {
                conditionStack.push(node);
            } else if (node.getUserObject().type == Token.And || node.getUserObject().type == Token.Or) {
                Node<Token> left = conditionStack.pop();
                Node<Token> right = conditionStack.pop();
                node.addNode(left);
                node.addNode(right);
                conditionStack.push(node);
            }
        }

        return conditionStack.pop();
    }

    /**
     * if higher push to stack, else pop till less than or equal, add to list push to stack if ( push to stack if ) pop to
     * list till (
     */
    private List<Node<Token>> getRPN(List<Node<Token>> nodeList) {
        List<Node<Token>> rpnList = new ArrayList<Node<Token>>();
        Stack<Node<Token>> operatorStack = new Stack<Node<Token>>();

        for (Node<Token> node : nodeList) {
            if (node.getUserObject().type == Token.Condition) {
                rpnList.add(node);

            } else if (node.getUserObject().type == Token.And) {
                operatorStack.push(node);
            } else if (node.getUserObject().type == Token.StartParenthesis) {
                operatorStack.push(node);
            } else if (node.getUserObject().type == Token.Or) {

                if (operatorStack.isEmpty() == false && operatorStack.peek().getUserObject().type == Token.And) {
                    do {
                        rpnList.add(operatorStack.pop());
                    } while (operatorStack.isEmpty() == false && operatorStack.peek().getUserObject().type == Token.And);
                }

                operatorStack.push(node);
            } else if (node.getUserObject().type == Token.EndParenthesis) {
                while (operatorStack.peek().getUserObject().type != Token.StartParenthesis) {
                    rpnList.add(operatorStack.pop());
                }
                operatorStack.pop();// pop the (
            }
        }
        if (operatorStack.isEmpty() == false) {
            do {
                rpnList.add(operatorStack.pop());
            } while (operatorStack.isEmpty() == false);
        }
        return rpnList;
    }

    private int findNodeIndex(List<Node<Token>> nodeList, int type) {
        int index = -1;
        for (Node<Token> node : nodeList) {
            index++;
            if (node.getUserObject().type == type) {
                return index;
            }
        }
        return index;
    }

    private boolean hasParenthesis(List<Node<Token>> nodeList) {
        boolean has = false;
        for (Node<Token> node : nodeList) {
            if (node.getUserObject().type == Token.StartParenthesis) {
                return true;
            }
        }
        return has;
    }

    private List<Node<Token>> toNodeList(List<Token> tokenList) {
        List<Node<Token>> nodeList = new ArrayList<Node<Token>>();
        for (Token token : tokenList) {
            Node<Token> node = new Node<Token>();
            node.setUserObject(token);
            nodeList.add(node);
        }
        return nodeList;
    }

    private void errorCheck(List<Token> tokenList) {
        if (tokenList.size() == 0) {
            errorMessageList.add("empty input");
        }
        if (tokenList.size() <= 2) {
            errorMessageList.add("input not complete");
        }
        if ((tokenList.get(0).type == Token.StartParenthesis 
                || tokenList.get(0).type == Token.Condition) == false) {
            errorMessageList.add("must start with ( or condition");
        }
        int lastIndex = tokenList.size() - 1;
        if ((tokenList.get(lastIndex).type == Token.EndParenthesis || tokenList.get(lastIndex).type == Token.Condition) == false) {
            errorMessageList.add("must end with ) or condition");
        }
        if (countToken(tokenList, Token.StartParenthesis) != countToken(tokenList, Token.EndParenthesis)) {
            errorMessageList.add("() not in pair");
        }
        // condition cannot duplicate
        for (int i = 1; i < tokenList.size(); i++) {
            Token token = tokenList.get(i);
            if (token.type == Token.And) {
                checkAnd(tokenList, i);
            } else if (token.type == Token.Or) {
                checkOr(tokenList, i);
            } else if (token.type == Token.StartParenthesis) {
                checkStartParenthesis(tokenList, i);
            } else if (token.type == Token.EndParenthesis) {
                checkEndParenthesis(tokenList, i);
            } else if (token.type == Token.Condition) {
                checkCondition(tokenList, i);
            }
        }
    }

    private int countToken(List<Token> tokenList, int type) {
        int count = 0;
        for (Token t : tokenList) {
            if (t.type == type) {
                count++;
            }
        }
        return count;
    }

    private void checkAnd(List<Token> tokenList, int currentIndex) {
        if ((tokenList.get(currentIndex - 1).type == Token.Condition || tokenList.get(currentIndex - 1).type == Token.EndParenthesis) == false) {
            errorMessageList.add("only ) and condition could sit before and");
        }
        if (currentIndex == tokenList.size() - 1) {
            return;
        }
        if ((tokenList.get(currentIndex + 1).type == Token.Condition || tokenList.get(currentIndex + 1).type == Token.StartParenthesis) == false) {
            errorMessageList.add("only ( and condition could sit after and");
        }

    }

    private void checkOr(List<Token> tokenList, int currentIndex) {
        if ((tokenList.get(currentIndex - 1).type == Token.Condition || tokenList.get(currentIndex - 1).type == Token.EndParenthesis) == false) {
            errorMessageList.add("only ) and condition could sit before or");
        }
        if (currentIndex == tokenList.size() - 1) {
            return;
        }
        if ((tokenList.get(currentIndex + 1).type == Token.Condition || tokenList.get(currentIndex + 1).type == Token.StartParenthesis) == false) {
            errorMessageList.add("only ( and condition could sit after or");
        }
    }

    private void checkStartParenthesis(List<Token> tokenList, int currentIndex) {
        if ((tokenList.get(currentIndex - 1).type == Token.And || tokenList.get(currentIndex - 1).type == Token.Or || tokenList.get(currentIndex - 1).type == Token.StartParenthesis) == false) {
            errorMessageList.add("only and, or, ( could sit before (");
        }
        if (currentIndex == tokenList.size() - 1) {
            return;
        }
        if ((tokenList.get(currentIndex + 1).type == Token.Condition || tokenList.get(currentIndex + 1).type == Token.StartParenthesis) == false) {
            errorMessageList.add("only ( and condition could sit after (");
        }

    }

    private void checkEndParenthesis(List<Token> tokenList, int currentIndex) {
        if ((tokenList.get(currentIndex - 1).type == Token.Condition || tokenList.get(currentIndex - 1).type == Token.EndParenthesis) == false) {
            errorMessageList.add("only condition and ) could sit before )");
        }
        if (currentIndex == tokenList.size() - 1) {
            return;
        }
        if ((tokenList.get(currentIndex + 1).type == Token.Or || tokenList.get(currentIndex + 1).type == Token.And || tokenList.get(currentIndex + 1).type == Token.EndParenthesis) == false) {
            errorMessageList.add("only ), and, or could sit after )");
        }

    }

    private void checkCondition(List<Token> tokenList, int currentIndex) {
        if ((tokenList.get(currentIndex - 1).type == Token.And || tokenList.get(currentIndex - 1).type == Token.Or || tokenList.get(currentIndex - 1).type == Token.StartParenthesis) == false) {
            errorMessageList.add("only and, or could sit before condition");
        }
        if (currentIndex == tokenList.size() - 1) {
            return;
        }
        if ((tokenList.get(currentIndex + 1).type == Token.Or || tokenList.get(currentIndex + 1).type == Token.And || tokenList.get(currentIndex + 1).type == Token.EndParenthesis) == false) {
            errorMessageList.add("only ), and, or could sit after condition");
        }

    }

    private List<Token> getTokenList(List<String> tokenValueList) {
        List<Token> tokenList = new ArrayList<Token>();
        for (String value : tokenValueList) {
            if (value.isEmpty()) {
                continue;
            }
            if ("(".equals(value)) {
                Token t = new Token();
                t.type = Token.StartParenthesis;
                tokenList.add(t);
            } else if (")".equals(value)) {
                Token t = new Token();
                t.type = Token.EndParenthesis;
                tokenList.add(t);

            } else if ("and".equals(value)) {
                Token t = new Token();
                t.type = Token.And;
                tokenList.add(t);

            } else if ("or".equals(value)) {
                Token t = new Token();
                t.type = Token.Or;
                tokenList.add(t);

            } else {
                Token t = new Token();
                t.type = Token.Condition;
                t.value = value;
                tokenList.add(t);

            }
        }
        return tokenList;
    }

    private List<String> getTokenValue(String expression) {
        expression = expression.toLowerCase();
        List<String> tokenValueList = new ArrayList<String>();
        StringBuffer tokenValue = new StringBuffer();
        for (int i = 0; i < expression.length(); i++) {

            char ch = expression.charAt(i);
            if (ch == ' ') {
                tokenValueList.add(tokenValue.toString());
                tokenValue = new StringBuffer();
            } else if (ch == '(' || ch == ')') {
                tokenValueList.add(tokenValue.toString());
                tokenValue = new StringBuffer();
                tokenValueList.add(String.valueOf(ch));
            } else {
                tokenValue.append(ch);
            }
        }
        tokenValueList.add(tokenValue.toString());
        return tokenValueList;
    }
}

class Token {
    public static int And = 1;
    public static int Or = 2;
    public static int StartParenthesis = 3;
    public static int EndParenthesis = 4;
    public static int Condition = 5;
    public String value;
    public int type;

    public String toString() {
        if (type == And) {
            return "and";
        } else if (type == Or) {
            return "or";
        } else if (type == StartParenthesis) {
            return "(";
        } else if (type == EndParenthesis) {
            return ")";
        } else if (type == Condition) {
            return value;
        }
        return "";
    }


}