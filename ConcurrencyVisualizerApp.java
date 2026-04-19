import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyVisualizerApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Frame().setVisible(true));
    }

    private static class Frame extends JFrame {
        private static final String DEMO_BLOCKING_QUEUE = "BlockingQueue (Producer/Consumer)";
        private static final String DEMO_ATOMIC_INTEGER = "AtomicInteger (Race vs Atomic)";
        private static final String DEMO_SEMAPHORE = "Semaphore (Permits)";
        private static final String DEMO_COUNTDOWN_LATCH = "CountDownLatch (Countdown + Release)";
        private static final String DEMO_CYCLIC_BARRIER = "CyclicBarrier (Two phases)";
        private static final String DEMO_SYNCHRONIZED = "Synchronized (wait/notify)";
        private static final String DEMO_CONDITION = "ConditionVariable (turn based)";
        private static final String DEMO_RWLOCK = "ReentrantReadWriteLock (read/write)";

        private final JComboBox<String> selector;
        private final JButton start;
        private final JButton reset;
        private final JSlider speed;
        private final JTextArea log;

        private final JPanel cards;

        // BlockingQueue widgets
        private final JTextField bqProducer = stateField();
        private final JTextField bqConsumer0 = stateField();
        private final JTextField bqConsumer1 = stateField();
        private final List<JLabel> bqSlots = new ArrayList<>();

        // Atomic widgets
        private final JTextField atomicWorkerA = stateField();
        private final JTextField atomicWorkerB = stateField();
        private final JTextField atomicPlain = stateField();
        private final JTextField atomicSafe = stateField();

        // Semaphore widgets
        private final JTextField semPermits = stateField();
        private final List<JLabel> semStations = new ArrayList<>();
        private final JTextField semQueue = stateField();
        private final JPanel semAnimationCanvas = new JPanel(null);
        private final Map<String, JLabel> semVehicles = new HashMap<>();

        // CountDownLatch widgets
        private final JTextField latchCount = stateField();
        private final JTextField latchStatus = stateField();

        // CyclicBarrier widgets
        private final JTextField barrierWaiting = stateField();
        private final JTextField barrierPhase = stateField();
        private final JPanel barrierAnimationCanvas = new JPanel(null);
        private final List<JLabel> barrierWorkers = new ArrayList<>();

        // Synchronized widgets
        private final JTextField syncA = stateField();
        private final JTextField syncB = stateField();
        private final JTextField syncLock = stateField();

        // Condition widgets
        private final JTextField condTurn = stateField();
        private final JTextField condResource = stateField();
        private final JTextField condC0 = stateField();
        private final JTextField condC1 = stateField();
        private final JTextField condC2 = stateField();

        // ReadWriteLock widgets
        private final JTextField rwReaders = stateField();
        private final JTextField rwWriter = stateField();
        private final JTextField rwBox = stateField();
        private final JPanel rwAnimationCanvas = new JPanel(null);
        private final List<JLabel> rwWorkers = new ArrayList<>();
        private final List<JLabel> rwQueueSlots = new ArrayList<>();

        private final Animator animator = new Animator();

        private Scenario running;

        Frame() {
            setTitle("Java Concurrency Visualizer");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(980, 650);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(8, 8));

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selector = new JComboBox<>(new String[]{
                    DEMO_BLOCKING_QUEUE,
                    DEMO_ATOMIC_INTEGER,
                    DEMO_SEMAPHORE,
                    DEMO_COUNTDOWN_LATCH,
                    DEMO_CYCLIC_BARRIER,
                    DEMO_SYNCHRONIZED,
                    DEMO_CONDITION,
                    DEMO_RWLOCK
            });
            start = new JButton("Start");
            reset = new JButton("Reset");
            speed = new JSlider(25, 300, 100);
            speed.setPreferredSize(new Dimension(180, 40));
            speed.setMajorTickSpacing(50);
            speed.setPaintTicks(true);
            top.add(new JLabel("Demo:"));
            top.add(selector);
            top.add(start);
            top.add(reset);
            top.add(new JLabel("Speed:"));
            top.add(speed);
            add(top, BorderLayout.NORTH);

            cards = new JPanel(new CardLayout());
            cards.add(buildBlockingQueuePanel(), DEMO_BLOCKING_QUEUE);
            cards.add(buildAtomicPanel(), DEMO_ATOMIC_INTEGER);
            cards.add(buildSemaphorePanel(), DEMO_SEMAPHORE);
            cards.add(buildCountDownLatchPanel(), DEMO_COUNTDOWN_LATCH);
            cards.add(buildCyclicBarrierPanel(), DEMO_CYCLIC_BARRIER);
            cards.add(buildSynchronizedPanel(), DEMO_SYNCHRONIZED);
            cards.add(buildConditionPanel(), DEMO_CONDITION);
            cards.add(buildRwLockPanel(), DEMO_RWLOCK);
            add(cards, BorderLayout.CENTER);

            log = new JTextArea();
            log.setEditable(false);
            JScrollPane sp = new JScrollPane(log);
            sp.setBorder(BorderFactory.createTitledBorder("Event log"));
            sp.setPreferredSize(new Dimension(980, 260));
            add(sp, BorderLayout.SOUTH);

            selector.addActionListener(e -> {
                ((CardLayout) cards.getLayout()).show(cards, (String) selector.getSelectedItem());
                log("Selected: " + selector.getSelectedItem());
                clearCurrentView();
            });
            start.addActionListener(e -> startScenario());
            reset.addActionListener(e -> resetScenario());

            log("Ready.");
        }

        private JPanel buildBlockingQueuePanel() {
            JPanel root = new JPanel(new BorderLayout(8, 8));
            JPanel actors = new JPanel();
            actors.setLayout(new BoxLayout(actors, BoxLayout.Y_AXIS));
            actors.setBorder(BorderFactory.createTitledBorder("Actors"));
            actors.add(new JLabel("Producer"));
            actors.add(bqProducer);
            actors.add(Box.createVerticalStrut(8));
            actors.add(new JLabel("Consumer0"));
            actors.add(bqConsumer0);
            actors.add(Box.createVerticalStrut(8));
            actors.add(new JLabel("Consumer1"));
            actors.add(bqConsumer1);

            JPanel queue = new JPanel(new FlowLayout(FlowLayout.LEFT));
            queue.setBorder(BorderFactory.createTitledBorder("Queue capacity 5"));
            for (int i = 0; i < 5; i++) {
                JLabel lbl = slot();
                bqSlots.add(lbl);
                queue.add(lbl);
            }

            root.add(actors, BorderLayout.WEST);
            root.add(queue, BorderLayout.CENTER);
            return root;
        }

        private JPanel buildAtomicPanel() {
            JPanel root = new JPanel();
            root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
            root.setBorder(BorderFactory.createTitledBorder("AtomicInteger visualization"));
            root.add(new JLabel("WorkerA"));
            root.add(atomicWorkerA);
            root.add(Box.createVerticalStrut(8));
            root.add(new JLabel("WorkerB"));
            root.add(atomicWorkerB);
            root.add(Box.createVerticalStrut(8));
            root.add(new JLabel("Plain int (race-prone)"));
            root.add(atomicPlain);
            root.add(Box.createVerticalStrut(8));
            root.add(new JLabel("AtomicInteger (safe)"));
            root.add(atomicSafe);
            return root;
        }

        private JPanel buildSemaphorePanel() {
            JPanel root = new JPanel(new BorderLayout(8, 8));
            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
            top.setBorder(BorderFactory.createTitledBorder("Semaphore state"));
            top.add(new JLabel("Permits available"));
            top.add(semPermits);
            top.add(Box.createVerticalStrut(8));
            top.add(new JLabel("Waiting queue"));
            top.add(semQueue);

            JPanel stations = new JPanel(new FlowLayout(FlowLayout.LEFT));
            stations.setBorder(BorderFactory.createTitledBorder("Charging stations (5 permits)"));
            for (int i = 0; i < 5; i++) {
                JLabel lbl = slot();
                semStations.add(lbl);
                stations.add(lbl);
            }
            semAnimationCanvas.setBorder(BorderFactory.createTitledBorder("Animation"));
            semAnimationCanvas.setPreferredSize(new Dimension(520, 180));
            semAnimationCanvas.setBackground(new Color(0xF7FAFF));
            root.add(top, BorderLayout.WEST);
            JPanel center = new JPanel(new BorderLayout());
            center.add(stations, BorderLayout.NORTH);
            center.add(semAnimationCanvas, BorderLayout.CENTER);
            root.add(center, BorderLayout.CENTER);
            return root;
        }

        private JPanel buildCountDownLatchPanel() {
            JPanel root = new JPanel();
            root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
            root.setBorder(BorderFactory.createTitledBorder("CountDownLatch state"));
            root.add(new JLabel("Current count"));
            root.add(latchCount);
            root.add(Box.createVerticalStrut(8));
            root.add(new JLabel("Status"));
            root.add(latchStatus);
            return root;
        }

        private JPanel buildCyclicBarrierPanel() {
            JPanel root = new JPanel(new BorderLayout(8, 8));
            JPanel left = new JPanel();
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            root.setBorder(BorderFactory.createTitledBorder("CyclicBarrier state"));
            left.add(new JLabel("Waiting threads"));
            left.add(barrierWaiting);
            left.add(Box.createVerticalStrut(8));
            left.add(new JLabel("Phase"));
            left.add(barrierPhase);
            barrierAnimationCanvas.setBorder(BorderFactory.createTitledBorder("Animation"));
            barrierAnimationCanvas.setBackground(new Color(0xF7FAFF));
            barrierAnimationCanvas.setPreferredSize(new Dimension(520, 220));
            root.add(left, BorderLayout.WEST);
            root.add(barrierAnimationCanvas, BorderLayout.CENTER);
            return root;
        }

        private JPanel buildSynchronizedPanel() {
            JPanel root = new JPanel();
            root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
            root.setBorder(BorderFactory.createTitledBorder("Synchronized wait/notify"));
            root.add(new JLabel("Thread A"));
            root.add(syncA);
            root.add(Box.createVerticalStrut(8));
            root.add(new JLabel("Thread B"));
            root.add(syncB);
            root.add(Box.createVerticalStrut(8));
            root.add(new JLabel("Lock state"));
            root.add(syncLock);
            return root;
        }

        private JPanel buildConditionPanel() {
            JPanel root = new JPanel();
            root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
            root.setBorder(BorderFactory.createTitledBorder("Condition variable turn-taking"));
            root.add(new JLabel("Current turn"));
            root.add(condTurn);
            root.add(Box.createVerticalStrut(8));
            root.add(new JLabel("Shared resource"));
            root.add(condResource);
            root.add(Box.createVerticalStrut(8));
            root.add(new JLabel("Consumer0"));
            root.add(condC0);
            root.add(new JLabel("Consumer1"));
            root.add(condC1);
            root.add(new JLabel("Consumer2"));
            root.add(condC2);
            return root;
        }

        private JPanel buildRwLockPanel() {
            JPanel root = new JPanel(new BorderLayout(8, 8));
            JPanel left = new JPanel();
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            root.setBorder(BorderFactory.createTitledBorder("ReentrantReadWriteLock"));
            left.add(new JLabel("Readers active"));
            left.add(rwReaders);
            left.add(Box.createVerticalStrut(8));
            left.add(new JLabel("Writer"));
            left.add(rwWriter);
            left.add(Box.createVerticalStrut(8));
            left.add(new JLabel("Box contents"));
            left.add(rwBox);
            rwAnimationCanvas.setBorder(BorderFactory.createTitledBorder("Animation"));
            rwAnimationCanvas.setBackground(new Color(0xF7FAFF));
            rwAnimationCanvas.setPreferredSize(new Dimension(520, 220));
            rwAnimationCanvas.setLayout(null);

            JLabel title = new JLabel("Shared queue of numbers", JLabel.CENTER);
            title.setBounds(10, 18, 500, 20);
            rwAnimationCanvas.add(title);

            rwQueueSlots.clear();
            for (int i = 0; i < 8; i++) {
                JLabel slot = new JLabel("-", JLabel.CENTER);
                slot.setOpaque(true);
                slot.setBackground(new Color(0xE3F2FD));
                slot.setBorder(BorderFactory.createLineBorder(new Color(0x64B5F6)));
                slot.setBounds(18 + i * 62, 45, 56, 32);
                rwQueueSlots.add(slot);
                rwAnimationCanvas.add(slot);
            }
            root.add(left, BorderLayout.WEST);
            root.add(rwAnimationCanvas, BorderLayout.CENTER);
            return root;
        }

        private JLabel slot() {
            JLabel lbl = new JLabel("empty", JLabel.CENTER);
            lbl.setPreferredSize(new Dimension(115, 45));
            lbl.setOpaque(true);
            lbl.setBackground(new Color(0xE9EEF6));
            lbl.setBorder(BorderFactory.createLineBorder(new Color(0x7D8EA8)));
            return lbl;
        }

        private JTextField stateField() {
            JTextField t = new JTextField("idle");
            t.setEditable(false);
            t.setMaximumSize(new Dimension(260, 30));
            return t;
        }

        private void startScenario() {
            if (running != null && running.isRunning()) {
                log("Scenario already running.");
                return;
            }
            clearCurrentView();
            String selected = (String) selector.getSelectedItem();
            Ui ui = new Ui(this);
            if (DEMO_BLOCKING_QUEUE.equals(selected)) {
                running = new BlockingQueueScenario(ui);
            } else if (DEMO_ATOMIC_INTEGER.equals(selected)) {
                running = new AtomicIntegerScenario(ui);
            } else if (DEMO_SEMAPHORE.equals(selected)) {
                running = new SemaphoreScenario(ui);
            } else if (DEMO_COUNTDOWN_LATCH.equals(selected)) {
                running = new CountDownLatchScenario(ui);
            } else if (DEMO_CYCLIC_BARRIER.equals(selected)) {
                running = new CyclicBarrierScenario(ui);
            } else if (DEMO_SYNCHRONIZED.equals(selected)) {
                running = new SynchronizedScenario(ui);
            } else if (DEMO_CONDITION.equals(selected)) {
                running = new ConditionScenario(ui);
            } else {
                running = new ReadWriteLockScenario(ui);
            }
            running.start();
        }

        private void resetScenario() {
            if (running != null) {
                running.stop();
            }
            clearCurrentView();
            log("Reset complete.");
        }

        private void clearCurrentView() {
            bqProducer.setText("idle");
            bqConsumer0.setText("idle");
            bqConsumer1.setText("idle");
            for (JLabel s : bqSlots) {
                s.setText("empty");
                s.setBackground(new Color(0xE9EEF6));
            }
            atomicWorkerA.setText("idle");
            atomicWorkerB.setText("idle");
            atomicPlain.setText("0");
            atomicSafe.setText("0");
            semPermits.setText("5");
            semQueue.setText("none");
            for (JLabel s : semStations) {
                s.setText("empty");
                s.setBackground(new Color(0xE9EEF6));
            }
            semAnimationCanvas.removeAll();
            semVehicles.clear();
            latchCount.setText("10");
            latchStatus.setText("idle");
            barrierWaiting.setText("0");
            barrierPhase.setText("phase 1");
            barrierAnimationCanvas.removeAll();
            barrierWorkers.clear();
            syncA.setText("idle");
            syncB.setText("idle");
            syncLock.setText("free");
            condTurn.setText("0");
            condResource.setText("10");
            condC0.setText("idle");
            condC1.setText("idle");
            condC2.setText("idle");
            rwReaders.setText("0");
            rwWriter.setText("idle");
            rwBox.setText("[1,2,3,4,5]");
            rwAnimationCanvas.removeAll();
            rwWorkers.clear();
            rwQueueSlots.clear();
            semAnimationCanvas.repaint();
            barrierAnimationCanvas.repaint();
            rwAnimationCanvas.repaint();
        }

        private void log(String message) {
            String ts = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
            log.append(ts + " | " + message + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    private interface Scenario {
        void start();
        void stop();
        boolean isRunning();
    }

    private static class Ui {
        private final Frame f;

        Ui(Frame f) { this.f = f; }

        int speedPercent() { return f.speed.getValue(); }

        void log(String m) { SwingUtilities.invokeLater(() -> f.log(m)); }

        void setBqProducer(String s) { SwingUtilities.invokeLater(() -> f.bqProducer.setText(s)); }
        void setBqConsumer(int i, String s) {
            SwingUtilities.invokeLater(() -> (i == 0 ? f.bqConsumer0 : f.bqConsumer1).setText(s));
        }
        void setBqQueue(List<String> items) {
            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < f.bqSlots.size(); i++) {
                    JLabel lbl = f.bqSlots.get(i);
                    if (i < items.size()) {
                        lbl.setText(items.get(i));
                        lbl.setBackground(new Color(0xBFE3C0));
                    } else {
                        lbl.setText("empty");
                        lbl.setBackground(new Color(0xE9EEF6));
                    }
                }
            });
        }

        void setAtomicWorker(String w, String s) {
            SwingUtilities.invokeLater(() -> {
                if ("A".equals(w)) f.atomicWorkerA.setText(s);
                else f.atomicWorkerB.setText(s);
            });
        }

        void setAtomicValues(int plain, int atomic) {
            SwingUtilities.invokeLater(() -> {
                f.atomicPlain.setText(String.valueOf(plain));
                f.atomicSafe.setText(String.valueOf(atomic));
            });
        }

        void setSemaphorePermits(int permits) { SwingUtilities.invokeLater(() -> f.semPermits.setText(String.valueOf(permits))); }
        void setSemaphoreQueue(String q) { SwingUtilities.invokeLater(() -> f.semQueue.setText(q)); }
        void setSemaphoreStations(String[] stations) {
            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < f.semStations.size(); i++) {
                    JLabel lbl = f.semStations.get(i);
                    String v = stations[i];
                    if ("empty".equals(v)) {
                        lbl.setText("empty");
                        lbl.setBackground(new Color(0xE9EEF6));
                    } else {
                        lbl.setText(v);
                        lbl.setBackground(new Color(0xF7D9A7));
                    }
                }
            });
        }

        void ensureSemaphoreVehicle(String ev) {
            SwingUtilities.invokeLater(() -> {
                if (!f.semVehicles.containsKey(ev)) {
                    JLabel l = token(ev, new Color(0xB3D9FF));
                    int idx = f.semVehicles.size();
                    l.setBounds(20 + (idx % 5) * 95, 25 + (idx / 5) * 35, 72, 24);
                    f.semVehicles.put(ev, l);
                    f.semAnimationCanvas.add(l);
                    f.semAnimationCanvas.repaint();
                }
            });
        }

        void moveSemaphoreVehicle(String ev, int x, int y) {
            SwingUtilities.invokeLater(() -> {
                JLabel l = f.semVehicles.get(ev);
                if (l != null) f.animator.animateTo(l, x, y);
            });
        }

        void setLatchCount(int count) { SwingUtilities.invokeLater(() -> f.latchCount.setText(String.valueOf(count))); }
        void setLatchStatus(String status) { SwingUtilities.invokeLater(() -> f.latchStatus.setText(status)); }

        void setBarrierWaiting(int waiting) { SwingUtilities.invokeLater(() -> f.barrierWaiting.setText(String.valueOf(waiting))); }
        void setBarrierPhase(String phase) { SwingUtilities.invokeLater(() -> f.barrierPhase.setText(phase)); }

        void initBarrierWorkers(int n) {
            SwingUtilities.invokeLater(() -> {
                f.barrierAnimationCanvas.removeAll();
                f.barrierWorkers.clear();
                for (int i = 0; i < n; i++) {
                    JLabel l = token("W" + i, new Color(0xCDECCF));
                    l.setBounds(20 + i * 90, 30, 72, 24);
                    f.barrierWorkers.add(l);
                    f.barrierAnimationCanvas.add(l);
                }
                f.barrierAnimationCanvas.repaint();
            });
        }

        void moveBarrierWorker(int id, int x, int y) {
            SwingUtilities.invokeLater(() -> {
                if (id < f.barrierWorkers.size()) f.animator.animateTo(f.barrierWorkers.get(id), x, y);
            });
        }

        void setSyncA(String s) { SwingUtilities.invokeLater(() -> f.syncA.setText(s)); }
        void setSyncB(String s) { SwingUtilities.invokeLater(() -> f.syncB.setText(s)); }
        void setSyncLock(String s) { SwingUtilities.invokeLater(() -> f.syncLock.setText(s)); }

        void setCondTurn(int turn) { SwingUtilities.invokeLater(() -> f.condTurn.setText(String.valueOf(turn))); }
        void setCondResource(int resource) { SwingUtilities.invokeLater(() -> f.condResource.setText(String.valueOf(resource))); }
        void setCondConsumer(int idx, String s) {
            SwingUtilities.invokeLater(() -> {
                if (idx == 0) f.condC0.setText(s);
                else if (idx == 1) f.condC1.setText(s);
                else f.condC2.setText(s);
            });
        }

        void setRwReaders(int count) { SwingUtilities.invokeLater(() -> f.rwReaders.setText(String.valueOf(count))); }
        void setRwWriter(String s) { SwingUtilities.invokeLater(() -> f.rwWriter.setText(s)); }
        void setRwBox(String s) { SwingUtilities.invokeLater(() -> f.rwBox.setText(s)); }

        void setRwQueueValues(List<Integer> values) {
            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < f.rwQueueSlots.size(); i++) {
                    JLabel slot = f.rwQueueSlots.get(i);
                    if (i < values.size()) {
                        slot.setText(String.valueOf(values.get(i)));
                        slot.setBackground(new Color(0xC8E6C9));
                    } else {
                        slot.setText("-");
                        slot.setBackground(new Color(0xE3F2FD));
                    }
                }
            });
        }

        void initRwWorkers(int n) {
            SwingUtilities.invokeLater(() -> {
                f.rwAnimationCanvas.removeAll();
                f.rwWorkers.clear();
                f.rwQueueSlots.clear();

                JLabel title = new JLabel("Shared queue of numbers", JLabel.CENTER);
                title.setBounds(10, 18, 500, 20);
                f.rwAnimationCanvas.add(title);
                for (int i = 0; i < 8; i++) {
                    JLabel slot = token("-", new Color(0xE3F2FD));
                    slot.setBounds(18 + i * 62, 45, 56, 32);
                    f.rwQueueSlots.add(slot);
                    f.rwAnimationCanvas.add(slot);
                }

                for (int i = 0; i < n; i++) {
                    JLabel l = token("T" + i, new Color(0xE7D3FF));
                    l.setBounds(20 + i * 95, 150, 72, 24);
                    f.rwWorkers.add(l);
                    f.rwAnimationCanvas.add(l);
                    f.rwAnimationCanvas.setComponentZOrder(l, 0);
                }
                f.rwAnimationCanvas.repaint();
            });
        }

        void moveRwWorker(int id, int x, int y) {
            SwingUtilities.invokeLater(() -> {
                if (id < f.rwWorkers.size()) {
                    JLabel token = f.rwWorkers.get(id);
                    f.rwAnimationCanvas.setComponentZOrder(token, 0);
                    f.rwAnimationCanvas.repaint();
                    f.animator.animateTo(token, x, y);
                }
            });
        }

        void setRwWorkerColor(int id, Color color) {
            SwingUtilities.invokeLater(() -> {
                if (id < f.rwWorkers.size()) {
                    JLabel token = f.rwWorkers.get(id);
                    token.setBackground(color);
                    token.repaint();
                }
            });
        }

        void setRwWorkerLabel(int id, String text) {
            SwingUtilities.invokeLater(() -> {
                if (id < f.rwWorkers.size()) {
                    JLabel token = f.rwWorkers.get(id);
                    token.setText(text);
                    token.repaint();
                }
            });
        }

        private JLabel token(String text, Color color) {
            JLabel l = new JLabel(text, JLabel.CENTER);
            l.setOpaque(true);
            l.setBackground(color);
            l.setBorder(BorderFactory.createLineBorder(new Color(0x546E7A)));
            return l;
        }
    }

    private static class Animator {
        private final Timer timer;
        private final List<Move> moves = new ArrayList<>();

        Animator() {
            timer = new Timer(30, e -> tick());
        }

        void animateTo(JLabel label, int x, int y) {
            moves.removeIf(m -> m.label == label);
            moves.add(new Move(label, x, y));
            if (!timer.isRunning()) timer.start();
        }

        private void tick() {
            if (moves.isEmpty()) {
                timer.stop();
                return;
            }
            List<Move> done = new ArrayList<>();
            for (Move m : moves) {
                Rectangle b = m.label.getBounds();
                int nx = approach(b.x, m.tx, 8);
                int ny = approach(b.y, m.ty, 8);
                m.label.setBounds(nx, ny, b.width, b.height);
                if (nx == m.tx && ny == m.ty) done.add(m);
            }
            moves.removeAll(done);
        }

        private int approach(int from, int to, int step) {
            if (Math.abs(to - from) <= step) return to;
            return from < to ? from + step : from - step;
        }

        private static class Move {
            JLabel label; int tx; int ty;
            Move(JLabel label, int tx, int ty) { this.label = label; this.tx = tx; this.ty = ty; }
        }
    }

    private static class BlockingQueueScenario implements Scenario {
        private static final String STOP = "producer stopped";
        private final Ui ui;
        private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(5);
        private final Deque<String> visual = new ArrayDeque<>();
        private volatile boolean running;
        private Thread producer;
        private Thread[] consumers;

        BlockingQueueScenario(Ui ui) { this.ui = ui; }
        public boolean isRunning() { return running; }

        public void start() {
            running = true;
            producer = new Thread(this::produce, "Producer");
            consumers = new Thread[]{new Thread(() -> consume(0), "Consumer0"), new Thread(() -> consume(1), "Consumer1")};
            producer.start();
            consumers[0].start();
            consumers[1].start();
        }

        public void stop() {
            running = false;
            if (producer != null) producer.interrupt();
            if (consumers != null) for (Thread t : consumers) t.interrupt();
        }

        private void produce() {
            ui.setBqProducer("running");
            for (int i = 1; i <= 20 && running; i++) {
                String item = "item" + i;
                try {
                    queue.put(item);
                    synchronized (visual) {
                        visual.addLast(item);
                        ui.setBqQueue(new ArrayList<>(visual));
                    }
                    ui.log("Producer added " + item + " [" + queue.size() + "/5]");
                    sleepScaled(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            while (running && !queue.isEmpty()) sleepScaled(80);
            for (int i = 0; i < 2 && running; i++) {
                try { queue.put(STOP); } catch (InterruptedException e) { return; }
            }
            ui.setBqProducer("done");
            ui.log("Producer finished.");
        }

        private void consume(int idx) {
            ui.setBqConsumer(idx, "running");
            while (running) {
                try {
                    String item = queue.take();
                    if (STOP.equals(item)) break;
                    synchronized (visual) {
                        visual.removeFirstOccurrence(item);
                        ui.setBqQueue(new ArrayList<>(visual));
                    }
                    ui.setBqConsumer(idx, "processing " + item);
                    ui.log("Consumer" + idx + " took " + item);
                    sleepScaled(500);
                    ui.setBqConsumer(idx, "running");
                } catch (InterruptedException e) {
                    break;
                }
            }
            ui.setBqConsumer(idx, "done");
            if (idx == 1) {
                running = false;
                ui.log("Scenario complete.");
            }
        }

        private void sleepScaled(int baseMs) {
            long ms = Math.max(10L, Math.round(baseMs * 100.0 / Math.max(25, ui.speedPercent())));
            try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
    }

    private static class AtomicIntegerScenario implements Scenario {
        private final Ui ui;
        private volatile boolean running;
        private Thread a;
        private Thread b;
        private int plain;
        private final AtomicInteger atomic = new AtomicInteger();

        AtomicIntegerScenario(Ui ui) { this.ui = ui; }
        public boolean isRunning() { return running; }

        public void start() {
            running = true;
            plain = 0;
            atomic.set(0);
            ui.setAtomicValues(plain, atomic.get());
            a = new Thread(() -> work("A"), "AtomicWorkerA");
            b = new Thread(() -> work("B"), "AtomicWorkerB");
            a.start();
            b.start();
        }

        public void stop() {
            running = false;
            if (a != null) a.interrupt();
            if (b != null) b.interrupt();
        }

        private void work(String w) {
            ui.setAtomicWorker(w, "running");
            for (int i = 0; i < 1000 && running; i++) {
                int read = plain;
                if ((i & 7) == 0) Thread.yield();
                plain = read + 1;
                int safe = atomic.incrementAndGet();
                if (i % 25 == 0) {
                    ui.setAtomicValues(plain, safe);
                    ui.log("Worker" + w + " incremented counters");
                }
                sleepScaled(3);
            }
            ui.setAtomicWorker(w, "done");
            ui.setAtomicValues(plain, atomic.get());
            if ("B".equals(w)) {
                running = false;
                ui.log("Atomic demo complete. plain=" + plain + ", atomic=" + atomic.get());
            }
        }

        private void sleepScaled(int baseMs) {
            long ms = Math.max(1L, Math.round(baseMs * 100.0 / Math.max(25, ui.speedPercent())));
            try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
    }

    private static class SemaphoreScenario implements Scenario {
        private final Ui ui;
        private final Semaphore semaphore = new Semaphore(5);
        private final String[] stations = new String[]{"empty", "empty", "empty", "empty", "empty"};
        private final List<String> waiting = new ArrayList<>();
        private volatile boolean running;
        private Thread dispatcher;
        private final List<Thread> workers = new ArrayList<>();

        SemaphoreScenario(Ui ui) { this.ui = ui; }
        public boolean isRunning() { return running; }

        public void start() {
            running = true;
            ui.setSemaphorePermits(5);
            ui.setSemaphoreStations(stations.clone());
            ui.setSemaphoreQueue("none");
            dispatcher = new Thread(this::dispatch, "EVDispatcher");
            dispatcher.start();
        }

        public void stop() {
            running = false;
            if (dispatcher != null) dispatcher.interrupt();
            for (Thread w : workers) w.interrupt();
        }

        private void dispatch() {
            for (int i = 0; i < 10 && running; i++) {
                String ev = "EV" + i;
                Thread t = new Thread(() -> charge(ev), ev);
                workers.add(t);
                t.start();
                sleepScaled(140);
            }
            for (Thread t : workers) {
                try { t.join(); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
            running = false;
            ui.log("Semaphore demo complete.");
        }

        private void charge(String ev) {
            if (!running) return;
            ui.ensureSemaphoreVehicle(ev);
            ui.moveSemaphoreVehicle(ev, 30, 95);
            synchronized (waiting) {
                waiting.add(ev);
                ui.setSemaphoreQueue(String.join(",", waiting));
            }
            try {
                semaphore.acquire();
                synchronized (waiting) {
                    waiting.remove(ev);
                    ui.setSemaphoreQueue(waiting.isEmpty() ? "none" : String.join(",", waiting));
                }
                int idx = claimStation(ev);
                ui.moveSemaphoreVehicle(ev, 35 + idx * 95, 135);
                ui.setSemaphorePermits(semaphore.availablePermits());
                ui.log(ev + " is charging..");
                sleepScaled(700);
                releaseStation(idx);
                ui.moveSemaphoreVehicle(ev, 430, 35 + (Math.abs(ev.hashCode()) % 100));
                semaphore.release();
                ui.setSemaphorePermits(semaphore.availablePermits());
                ui.log(ev + " finished charging.");
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        private int claimStation(String ev) {
            synchronized (stations) {
                for (int i = 0; i < stations.length; i++) {
                    if ("empty".equals(stations[i])) {
                        stations[i] = ev;
                        ui.setSemaphoreStations(stations.clone());
                        return i;
                    }
                }
                return -1;
            }
        }

        private void releaseStation(int idx) {
            synchronized (stations) {
                if (idx >= 0) {
                    stations[idx] = "empty";
                    ui.setSemaphoreStations(stations.clone());
                }
            }
        }

        private void sleepScaled(int baseMs) {
            long ms = Math.max(10L, Math.round(baseMs * 100.0 / Math.max(25, ui.speedPercent())));
            try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
    }

    private static class CountDownLatchScenario implements Scenario {
        private final Ui ui;
        private volatile boolean running;
        private Thread coordinator;
        private final List<Thread> workers = new ArrayList<>();

        CountDownLatchScenario(Ui ui) { this.ui = ui; }
        public boolean isRunning() { return running; }

        public void start() {
            running = true;
            final int tasks = 10;
            CountDownLatch latch = new CountDownLatch(tasks);
            ui.setLatchCount(tasks);
            ui.setLatchStatus("running");

            coordinator = new Thread(() -> {
                for (int i = 0; i < tasks && running; i++) {
                    final int taskId = i;
                    Thread t = new Thread(() -> {
                        sleepScaled(150 + (taskId % 5) * 120);
                        latch.countDown();
                        ui.setLatchCount((int) latch.getCount());
                        ui.log("Task-" + taskId + " finished. count=" + latch.getCount());
                    }, "LatchTask-" + i);
                    workers.add(t);
                    t.start();
                }
                try {
                    latch.await();
                    if (running) {
                        ui.setLatchStatus("released");
                        ui.log("CountDownLatch reached zero. Released.");
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    running = false;
                }
            }, "LatchCoordinator");
            coordinator.start();
        }

        public void stop() {
            running = false;
            if (coordinator != null) coordinator.interrupt();
            for (Thread t : workers) t.interrupt();
        }

        private void sleepScaled(int baseMs) {
            long ms = Math.max(10L, Math.round(baseMs * 100.0 / Math.max(25, ui.speedPercent())));
            try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
    }

    private static class CyclicBarrierScenario implements Scenario {
        private final Ui ui;
        private volatile boolean running;
        private final List<Thread> workers = new ArrayList<>();

        CyclicBarrierScenario(Ui ui) { this.ui = ui; }
        public boolean isRunning() { return running; }

        public void start() {
            running = true;
            final int parties = 5;
            ui.initBarrierWorkers(parties);
            final AtomicInteger phase = new AtomicInteger(1);
            CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
                if (phase.get() == 1) {
                    phase.set(2);
                    ui.setBarrierPhase("phase 2");
                    ui.log("Barrier opened. Moving to phase 2.");
                } else {
                    ui.setBarrierPhase("done");
                    ui.log("Second barrier opened. Scenario complete.");
                    running = false;
                }
            });

            ui.setBarrierPhase("phase 1");
            ui.setBarrierWaiting(0);

            for (int i = 0; i < parties; i++) {
                final int id = i;
                Thread t = new Thread(() -> {
                    try {
                        ui.log("Worker" + id + " did phase-1 work");
                        ui.moveBarrierWorker(id, 20 + id * 90, 70);
                        sleepScaled(180 + id * 60);
                        ui.moveBarrierWorker(id, 20 + id * 90, 120);
                        ui.setBarrierWaiting(barrier.getNumberWaiting() + 1);
                        barrier.await();

                        ui.log("Worker" + id + " did phase-2 work");
                        ui.moveBarrierWorker(id, 20 + id * 90, 160);
                        sleepScaled(180 + id * 70);
                        ui.moveBarrierWorker(id, 20 + id * 90, 195);
                        ui.setBarrierWaiting(barrier.getNumberWaiting() + 1);
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }, "BarrierWorker-" + i);
                workers.add(t);
                t.start();
            }
        }

        public void stop() {
            running = false;
            for (Thread t : workers) t.interrupt();
        }

        private void sleepScaled(int baseMs) {
            long ms = Math.max(10L, Math.round(baseMs * 100.0 / Math.max(25, ui.speedPercent())));
            try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
    }

    private static class SynchronizedScenario implements Scenario {
        private final Ui ui;
        private volatile boolean running;
        private Thread a;
        private Thread b;
        private final Object lock = new Object();

        SynchronizedScenario(Ui ui) { this.ui = ui; }
        public boolean isRunning() { return running; }

        public void start() {
            running = true;
            a = new Thread(() -> {
                ui.setSyncA("working");
                synchronized (lock) {
                    ui.setSyncLock("owned by A");
                    ui.log("Thread A is doing some work.");
                    sleepScaled(300);
                    ui.setSyncA("waiting notify");
                    ui.log("Thread A waiting.");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    ui.setSyncA("resumed");
                    ui.log("Thread A resumed.");
                }
                ui.setSyncLock("free");
                ui.setSyncA("done");
            }, "SyncA");

            b = new Thread(() -> {
                sleepScaled(450);
                ui.setSyncB("working");
                synchronized (lock) {
                    ui.setSyncLock("owned by B");
                    ui.log("Thread B is notifying A.");
                    lock.notify();
                    ui.setSyncB("notified");
                }
                ui.setSyncLock("free");
                ui.setSyncB("done");
                running = false;
            }, "SyncB");

            a.start();
            b.start();
        }

        public void stop() {
            running = false;
            if (a != null) a.interrupt();
            if (b != null) b.interrupt();
        }
    }

    private static class ConditionScenario implements Scenario {
        private final Ui ui;
        private volatile boolean running;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition turnChanged = lock.newCondition();
        private int turn = 0;
        private int resource = 10;
        private final List<Thread> workers = new ArrayList<>();

        ConditionScenario(Ui ui) { this.ui = ui; }
        public boolean isRunning() { return running; }

        public void start() {
            running = true;
            turn = 0;
            resource = 10;
            ui.setCondTurn(turn);
            ui.setCondResource(resource);
            for (int i = 0; i < 3; i++) {
                final int idx = i;
                Thread t = new Thread(() -> runConsumer(idx), "CondConsumer-" + idx);
                workers.add(t);
                t.start();
            }
        }

        private void runConsumer(int idx) {
            while (running) {
                lock.lock();
                try {
                    while (running && turn != idx) {
                        ui.setCondConsumer(idx, "waiting turn");
                        turnChanged.await();
                    }
                    if (!running || resource <= 0) {
                        turnChanged.signalAll();
                        break;
                    }
                    ui.setCondConsumer(idx, "consuming");
                    resource--;
                    ui.setCondResource(resource);
                    ui.log("Consumer " + idx + " took item " + (resource + 1));
                    turn = (turn + 1) % 3;
                    ui.setCondTurn(turn);
                    turnChanged.signalAll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    lock.unlock();
                }
                sleepScaled(220);
            }
            ui.setCondConsumer(idx, "done");
            if (idx == 2) running = false;
        }

        public void stop() {
            running = false;
            for (Thread t : workers) t.interrupt();
            lock.lock();
            try { turnChanged.signalAll(); } finally { lock.unlock(); }
        }
    }

    private static class ReadWriteLockScenario implements Scenario {
        private final Ui ui;
        private volatile boolean running;
        private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        private final List<Integer> box = new ArrayList<>();
        private final List<Thread> workers = new ArrayList<>();
        private final AtomicInteger activeReaders = new AtomicInteger();
        private final Random random = new Random();

        ReadWriteLockScenario(Ui ui) {
            this.ui = ui;
            box.add(1); box.add(2); box.add(3); box.add(4); box.add(5);
        }
        public boolean isRunning() { return running; }

        public void start() {
            running = true;
            ui.initRwWorkers(5);
            ui.setRwBox(box.toString());
            ui.setRwQueueValues(new ArrayList<>(box));
            for (int i = 0; i < 5; i++) {
                final int id = i;
                Thread t = new Thread(() -> runWorker(id), "RWWorker-" + id);
                workers.add(t);
                t.start();
            }
        }

        private void runWorker(int id) {
            for (int step = 0; step < 5 && running; step++) {
                int laneX = 20 + id * 95;
                int op = random.nextInt(4);
                if (op <= 1) {
                    // READ -> blue
                    ui.setRwWorkerColor(id, new Color(0x90CAF9));
                    ui.setRwWorkerLabel(id, "(R)");
                    ui.moveRwWorker(id, laneX, 90);
                    sleepScaled(140);
                    rw.readLock().lock();
                    int readers = activeReaders.incrementAndGet();
                    ui.setRwReaders(readers);
                    try {
                        ui.log("Worker" + id + " read box " + box);
                        ui.setRwBox(box.toString());
                        sleepScaled(240);
                    } finally {
                        activeReaders.decrementAndGet();
                        ui.setRwReaders(activeReaders.get());
                        rw.readLock().unlock();
                    }
                } else {
                    boolean addOperation = op == 2;
                    // ADD -> green, REMOVE -> red
                    ui.setRwWorkerColor(id, addOperation ? new Color(0xA5D6A7) : new Color(0xEF9A9A));
                    if (addOperation) {
                        int preview = random.nextInt(9) + 1;
                        ui.setRwWorkerLabel(id, "(+" + preview + ")");
                    } else {
                        int preview = box.isEmpty() ? 0 : box.get(0);
                        ui.setRwWorkerLabel(id, "(-" + preview + ")");
                    }
                    ui.moveRwWorker(id, laneX, 90);
                    sleepScaled(160);
                    rw.writeLock().lock();
                    ui.setRwWriter("Worker" + id + " writing");
                    try {
                        if (addOperation) {
                            int value = random.nextInt(9) + 1;
                            box.add(value);
                            ui.log("Worker" + id + " added " + value);
                        } else if (!box.isEmpty()) {
                            int removed = box.remove(0);
                            ui.log("Worker" + id + " removed " + removed);
                        }
                        ui.setRwBox(box.toString());
                        ui.setRwQueueValues(new ArrayList<>(box));
                        sleepScaled(280);
                    } finally {
                        ui.setRwWriter("idle");
                        rw.writeLock().unlock();
                    }
                }
                ui.moveRwWorker(id, laneX, 150);
                // idle/default
                ui.setRwWorkerColor(id, new Color(0xE7D3FF));
                ui.setRwWorkerLabel(id, "T" + id);
                sleepScaled(170);
            }
            if (id == 4) {
                running = false;
                ui.log("ReadWriteLock scenario complete.");
            }
        }

        public void stop() {
            running = false;
            for (Thread t : workers) t.interrupt();
        }
    }

    private static void sleepScaled(Ui ui, int baseMs) {
        long ms = Math.max(10L, Math.round(baseMs * 100.0 / Math.max(25, ui.speedPercent())));
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static void sleepScaled(int baseMs) {
        try { Thread.sleep(baseMs); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}