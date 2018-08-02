package io.choerodon.manager.domain.manager.entity;

/**
 * @author superlee
 */
public class MyLinkedList<T> {

    Node head = null; // 头节点

    /**
     * 向链表中插入数据
     *
     * @param data
     */
    public void addNode(T data) {
        Node newNode = new Node(data);// 实例化一个节点
        if (head == null) {
            head = newNode;
            return;
        }
        Node tmp = head;
        while (tmp.next != null) {
            tmp = tmp.next;
        }
        tmp.next = newNode;
    }

    /**
     * 判断链表是否有环，单向链表有环时，尾节点相同
     *
     * @return
     */
    public boolean isLoop() {
        Node fast = head, slow = head;
        if (fast == null) {
            return false;
        }
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast != null && fast.data.equals(slow.data)) {
                return true;
            }
        }
        return !(fast == null || fast.next == null);
    }

    class Node {
        Node next = null;// 节点的引用，指向下一个节点
        T data;// 节点的对象，即内容

        public Node(T data) {
            this.data = data;
        }
    }
}
