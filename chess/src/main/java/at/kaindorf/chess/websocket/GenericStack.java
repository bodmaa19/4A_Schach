package at.kaindorf.chess.websocket;

import java.util.ArrayList;
import java.util.List;

public class GenericStack<T>  {
    private int topOfStack;
    private int size;
    private List<T> elements;


    public GenericStack(int size) {
        this.size = size;
        topOfStack = -1;
        elements = new ArrayList<>();
    }

    public GenericStack() {
        this.size = 10;
        topOfStack = -1;
        elements = new ArrayList<>();
    }

    public void add(T element) {
        if(elements.size() + 1 > size) {
            throw new IndexOutOfBoundsException("Der Stack ist nicht groß genug");
        }

        elements.add(element);
        topOfStack++;
    }

    public T get() {
        if(topOfStack == -1) {
            throw new IndexOutOfBoundsException("Es wurden bereits alle Elemente entfernt");
        }

        T element = elements.get(topOfStack);
        elements.remove(topOfStack);
        topOfStack--;
        return element;
    }

    public boolean containsElement(T element) {
        return elements.contains(element);
    }

    public List<T> getList() {
        return elements;
    }

    public int getNumberOfElements() {
        return elements.size();
    }

    @Override
    public String toString() {
        String output = "STACK:" + "\n";
        for(T element : elements) {
            output += "  - " + element + "\n";
        }
        return output;
    }
}

