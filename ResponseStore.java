import java.util.LinkedList;
import java.util.Queue;

class ResponseStore {
    static final Queue<String> responses = new LinkedList<>();
    static final Object lock = new Object();
}