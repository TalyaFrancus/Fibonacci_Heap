package src;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    private HeapNode min;
    public static int links;
    private int size;
    private int trees;
    private int marked;
    private HeapNode first;
    private static int cuts;

    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *
     */
    public boolean isEmpty() //return if the heap is empty
    {
        return this.min == null;
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     */
    public HeapNode insert(int key)  // insert new heapNode with the given key to the heap
    {
        HeapNode node = new HeapNode(key, 0, null, null, null);		// create new heap with one node
        node.setNext(node);
        node.setPrev(node);
        FibonacciHeap heap2 = createOneNodeHeap(node);
        meld(heap2);	// meld new heap to current heap
        this.first = node;	// change first to new node added

        return node;
    }

    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     *
     */
    public void deleteMin()  //delete the heap with the min key in the heap and fix the heap to legal heap
    {
        if (this.isEmpty()){
            return;
        }
        else if (this.min.getChild() == null && this.min.getNext() == min){
            this.first = null;
            this.min = null;
            this.size = 0;
            this.trees = 0;
            return;
        }
        else if (this.min.getChild() == null){
            this.min.getPrev().setNext(min.getNext());
            this.min.getNext().setPrev(min.getPrev());
            if (this.first == this.min){
                this.first = min.getNext();
            }
            this.min = this.first;
            this.size--;
            fromBucket();
            return;
        }
        else if (this.min.getNext() == this.min){
        	HeapNode x = this.min.getChild();
            do{
                if (x.getMark()) {
                    x.setMark(false);
                    this.marked--;
                }
                x.setParent(null);
                x = x.getNext();
            } while (x != min.getChild());
            this.first = this.min.getChild();
            this.min = this.first;
            this.size--;
            fromBucket();
            return;
        }

        else{
            this.min.getPrev().setNext(min.getChild());
            this.min.getNext().setPrev(min.getChild().prev);
            this.min.getChild().getPrev().setParent(null);
            this.min.getChild().getPrev().setNext(min.getNext());
            this.min.getChild().setParent(null);
            this.min.getChild().setPrev(min.getPrev());
            this.trees += min.getRank();

        }
        if (this.first == min){
            this.first = min.getChild();
        }
        HeapNode x = this.min.getChild();
        do{
            if (x.getMark()) {
                x.setMark(false);
                this.marked--;
            }
            x.setParent(null);
            x = x.getNext();
        } while (x != min.getChild());
        this.min = this.first;
        this.size--;
        fromBucket();

    }

    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     *
     */
    public HeapNode findMin() // return the min heapNode in the heap
    {
        return this.min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
    public void meld (FibonacciHeap heap2)  //add the given heap to the current heap (to left)
    {
        if (heap2.isEmpty()){	// if heap2 empty, do nothing
            return;
        }

        if (this.isEmpty()){	// case this is empty heap make this ==(deep) heap2
            this.min = heap2.min;
            this.trees = heap2.trees;
            this.marked = heap2.marked;
            this.size = heap2.size;
            this.first = heap2.first;
            return;
        }
        HeapNode temp = this.first.prev;	// else, change values as needed
        this.first.prev.next = heap2.first;
        this.first.prev = heap2.first.prev;
        heap2.first.prev.next = this.first;
        heap2.first.prev = temp;

        this.size += heap2.size;
        this.marked += heap2.marked;
        this.trees += heap2.trees;
        this.min = this.min.getKey() >= heap2.min.getKey() ? heap2.min : this.min;


    }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     */
    public int size() //return the number of heapNode in the heap
    {
        return this.size;
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     *
     */
    public int[] countersRep()	// returns array where A[i] := #trees in heap from rank i
    {
    	if(this.isEmpty())	// case heap is empty
    		return new int[0];
    	int maxRank = this.maxRankOfTree();		// find max rank and initiate array
        int[] arr = new int[maxRank+1];
        HeapNode node = this.first;		// fill array
        arr[node.getRank()] = 1;
        node = node.next;
        while (node != this.first) {
            arr[node.rank] += 1;
            node = node.next;
        }

        return arr;
    }
     
    private int maxRankOfTree() //return the max rank of tree in the heap.
    {
    	int maxRank = 0;
    	HeapNode x = this.first;
    	do {
    		maxRank = Math.max(maxRank, x.rank);
    		x = x.getNext();
    	}
    	while(x != this.first);
    	return maxRank;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     *
     */
    public void delete(HeapNode x) //delete the given node from the heap and fix the heap to a legal heap
    {
        this.decreaseKey(x, x.getKey()-min.getKey()+1);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta) {
        x.key = x.key - delta;	// decrease key value
        if(x.key < this.min.key)	// change minimum accordingly
            this.min = x;
        if(x.getParent() == null || x.key >= x.getParent().key)	// case no violation of heap rule
            return;
        this.cascadingCuts(x, x.getParent());	// if violation, perform cascading cuts
    }

    private void cascadingCuts(HeapNode x, HeapNode y) {	// perform cascading cuts on x and y (assume y = x.parent)
        this.cut(x, y);		// cut x from y
        if(y.getParent() != null) {	// if y not root
            if(!y.mark) { 		// if not marked
                y.mark = true;
                this.marked++;
            }
            else	// if marked, recursively perform cascading cuts on y, y.parent
                cascadingCuts(y, y.getParent());
        }
    }



    private void cut(HeapNode x, HeapNode y) {		// cut node x from node y (assume y = x.parent)
        if(y.getChild() == x && y.getRank() > 1) {
            y.setChild(x.next);
        }
        else if (y.getRank() == 1)	// case x the only child of y
            y.setChild(null);
        x.getNext().setPrev(x.getPrev());	// change pointers
        x.getPrev().setNext(x.getNext());
        y.setRank(y.getRank() - 1);
        if(x.getMark())		// x is now a root, so if marked - make it unmarked
            this.marked--;
        x.setMark(false);
        x.setParent(null);	// add x as new tree (first) in heap
        x.setNext(this.first);
        x.setPrev(this.first.getPrev());
        this.first.getPrev().setNext(x);
        this.first.setPrev(x);
        this.first = x;
        cuts++;	// update cuts and number of trees
        this.trees++;
    }

    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     *
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential() //return the potential of the heap
    {
        return this.trees + 2*this.marked;
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks()
    {
        return links;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts()
    {
        return cuts;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k)  //return an array with the k min key in the heap
    {
        int[] arr = new int[k];
        FibonacciHeap heap = new FibonacciHeap();	// help Fibonacci heap
        HeapNode node = heap.insert(H.min.key);
        node.setHeapNodeInOrigin(H.min);
        HeapNode node1 = null;
        for (int i=0; i<k; i++){
            node1 = heap.min.getHeapNodeInOrigin();
            heap.deleteMin();
            arr[i] = node1.getKey();
            if (node1.getChild() == null){
                continue;
            }
            node = heap.insert(node1.getChild().getKey());
            node.setHeapNodeInOrigin(node1.getChild());
            HeapNode child = node1.getChild().getNext();
            while (child != node1.getChild()){
                node = heap.insert(child.getKey());
                node.setHeapNodeInOrigin(child);
                child = child.getNext();
            }

        }

        return arr;
    }


    private HeapNode link(HeapNode x, HeapNode y) //connnect the 2 given heapNode to one.
    {
        if (x.getKey() < y.getKey()){
            return linkBigSmall(x, y);
        }
        else {
            return linkBigSmall(y, x);
        }

    }

    private HeapNode linkBigSmall (HeapNode x, HeapNode y) //the func make the left given node to the parent of the left given node
    {
        if (y.getParent() == null){
            y.setNext(y);
            y.setPrev(y);
        }
        if (x.getChild() == null){
            x.setChild(y);
            y.setParent(x);
        }
        else {
            y.setNext(x.child.getNext());
            x.getChild().getNext().setPrev(y);
            x.getChild().setNext(y);
            y.setPrev(x.getChild());
            y.setParent(x);

        }
        x.setChild(y);
        x.setRank(x.getRank()+1);
        links++;
        return x;

    }

    /**
     * //the func take every tree in the heap and put it in the rank-th entry in the array and if there
     * is another tree the func make a link between them
     */


    public HeapNode[] toBucket()
    {
        HeapNode[] bucket = new HeapNode[(int)(Math.log((double) this.size)/Math.log(1.61))+1];
        first.getPrev().setNext(null);
        HeapNode x = first;
        HeapNode y = x;
        this.min = first;
        while (x != null){
            y = x;
            x = x.getNext();
            y.setPrev(y);
            y.setNext(y);
            if (y.getKey() < this.min.getKey()){
                this.min = y;
            }

            while (bucket[y.getRank()] != null){
                y = link(y, bucket[y.getRank()]);
                bucket[y.getRank()-1] = null;
            }
            bucket[y.getRank()] = y;

        }
        return bucket;
    }

    private void fromBucket()  //the func get the array we create in toBucket and connect all the tree to one heap
    {
        HeapNode node = null;
        HeapNode[] bucket = toBucket();

        for (int i=0; i<bucket.length; i++){
            if (bucket[i] != null){
                if (node == null){
                    node = bucket[i];
                    node.setNext(node);
                    node.setPrev(node);
                    this.first = node;
                    this.min = node;
                    this.trees = 1;
                }
                else {
                    HeapNode node1 = bucket[i];
                    this.first.prev.setNext(node1);
                    node1.setPrev(this.first.prev);
                    this.first.setPrev(node1);
                    node1.setNext(this.first);
                    this.trees++;
                    if (bucket[i].getKey() < this.min.getKey()){
                        this.min = bucket[i];
                    }
                }
            }
        }
    }

    private FibonacciHeap createOneNodeHeap(HeapNode node) //the func get node and create a new heap of it.
    {
        FibonacciHeap heap2 = new FibonacciHeap();
        heap2.first = node;
        heap2.min = node;
        heap2.size = 1;
        heap2.marked = 0;
        heap2.trees = 1;

        return heap2;
    }

    public HeapNode getFirst(){
        return this.first;
    }




    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     *
     */
    public static class HeapNode{

        private int key;
        private int rank;
        private boolean mark;
        private HeapNode child;
        private HeapNode prev;
        private HeapNode next;
        private HeapNode parent;
        private HeapNode HeapNodeInOrigin;	// used only for kMin function



        public HeapNode(int key, int rank, HeapNode next, HeapNode prev, HeapNode parent) {
            this.key = key;
            this.rank = rank;
            this.mark = false;
            this.child = null;
            this.next = next;
            this.prev = prev;
            this.parent = parent;
            this.HeapNodeInOrigin = null;


        }

        public int getKey() {
            return this.key;
        }

        public void setKey(int k){
            this.key = k;
        }


        public int getRank(){
            return this.rank;
        }

        public void setRank(int k){
            this.rank = k;
        }

        public boolean getMark(){
            return this.mark;
        }

        public void setMark(boolean bool){
            this.mark = bool;
        }

        public HeapNode getChild(){
            return this.child;
        }

        public void setChild(HeapNode node){
            this.child = node;
        }

        public HeapNode getNext(){
            return this.next;
        }

        public void setNext(HeapNode node){
            this.next = node;
        }

        public HeapNode getPrev(){
            return this.prev;
        }

        public void setPrev(HeapNode node){
            this.prev = node;
        }

        public HeapNode getParent(){
            return this.parent;
        }

        public void setParent(HeapNode node){
            this.parent = node;
        }

        private HeapNode getHeapNodeInOrigin(){		// used only for kMin function
            return this.HeapNodeInOrigin;
        }

        private void setHeapNodeInOrigin(HeapNode node){	// used only for kMin function
            this.HeapNodeInOrigin = node;
        }


    }
}
