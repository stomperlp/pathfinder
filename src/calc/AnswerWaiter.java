package calc;

/**
 * A synchronization utility that allows one thread to wait for an answer 
 * from another thread. This implements a simple producer-consumer pattern
 * for inter-thread communication.
 */
public class AnswerWaiter {
    // Flag indicating whether an answer has been received
    private boolean answerReceived = false;
    // The answer object being passed between threads
    private Object answer;

    /**
     * Causes the current thread to wait until an answer is provided.
     * 
     * @return The answer object provided by another thread
     * @throws InterruptedException if the waiting thread is interrupted
     */
    public synchronized Object waitForAnswer() throws InterruptedException {
        // Reset state for new wait
        answerReceived = false;
        answer = null;
        
        // Wait until answer is provided
        while (!answerReceived) {
            wait(); // Releases the lock and waits for notification
        }
        return answer;
    }

    /**
     * Provides an answer to waiting threads and notifies them.
     * 
     * @param answer The object to be returned to waiting threads
     */
    public synchronized void provideAnswer(Object answer) {
        this.answer = answer;
        this.answerReceived = true;
        notifyAll(); // Wakes up all threads waiting on this object's monitor
    }
}