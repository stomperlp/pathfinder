package calc;

public class AnswerWaiter {
    private boolean answerReceived = false;
    private Object answer;

    public synchronized Object waitForAnswer() throws InterruptedException {
        answerReceived = false;
        answer = null;
        while (!answerReceived) {
            wait(); // Releases lock and waits
        }
        return answer;
    }

    public synchronized void provideAnswer(Object answer) {
        this.answer = answer;
        this.answerReceived = true;
        notifyAll(); // Notifies all waiting threads
    }
}