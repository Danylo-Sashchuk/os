import java.util.HashMap;
import java.util.Map;

public class ProcessorTimeQuantum {
    private static final Map<Integer, Integer> processorTimeQuantumMap = new HashMap<>();

    static {
        // Initialize processor time quantums
        processorTimeQuantumMap.put(1, 2);
        processorTimeQuantumMap.put(2, 4);
        processorTimeQuantumMap.put(3, 3);
        processorTimeQuantumMap.put(4, 2);
        processorTimeQuantumMap.put(5, 2);
        processorTimeQuantumMap.put(6, 4);
        processorTimeQuantumMap.put(7, 3);
        processorTimeQuantumMap.put(8, 2);
    }

    public static int getTimeQuantum(int processorId) {
        return processorTimeQuantumMap.getOrDefault(processorId, 2);
    }
}
