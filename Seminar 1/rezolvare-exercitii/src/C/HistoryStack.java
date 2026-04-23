package C;

import java.util.ArrayDeque;
import java.util.Optional;

public class HistoryStack<T> {
    private final ArrayDeque<T> stack = new ArrayDeque<>();
    private final ArrayDeque<T> history = new ArrayDeque<>();

    public void push(T item){
        this.stack.push(item);
    }

    public Optional<T> pop(){
        if(this.stack.isEmpty()){
            return Optional.empty();
        }

        T item = stack.pop();
        history.push(item);

        return Optional.of(item);
    }

    public Optional<T> undo(){
        if(history.isEmpty()){
            return Optional.empty();
        }

        T item = history.pop();
        stack.push(item);

        return Optional.of(item);
    }

    public Optional<T> peek(){
        if(stack.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(stack.peek());
    }

    public int size(){
        return stack.size();
    }

    public int historySize(){
        return this.history.size();
    }
}
