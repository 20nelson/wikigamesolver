import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker.StateValue;
import javax.swing.border.EmptyBorder;

import org.jsoup.Jsoup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WikigameSolver extends JFrame {

	private JPanel contentPane;
	private JTextField url;
	private WikigameWorker ww;
	private JTextField target;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WikigameSolver frame = new WikigameSolver();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WikigameSolver() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(false);
		progressBar.setIndeterminate(true);
		
		url = new JTextField();
		url.setColumns(10);
		JList<String> path = new JList<String>();
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				path.setListData(new String[0]);
				if(ww != null && !ww.isDone()){
					ww.cancel(true);
				}
				ww = new WikigameWorker(url.getText(), target.getText(), progressBar);
				progressBar.setStringPainted(true);
				progressBar.setIndeterminate(false);
				ww.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						switch (evt.getPropertyName()) {
						case "state":
							switch ((StateValue) evt.getNewValue()) {
							case DONE:
								try {
									path.setListData(ww.get());
									progressBar.setStringPainted(false);
									progressBar.setIndeterminate(true);
								} catch (Exception e) {
									// TODO Auto-generated catch block
								}
								break;

							default:
								break;
							}
							break;
						case "progress":
							System.out.println((Integer)evt.getNewValue());
						default:
							break;
						}

					}
				});
				ww.execute();
			}
		});
		
		JButton btnAbort = new JButton("Abort");
		btnAbort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ww.cancel(true);
				path.setListData(new String[0]);
				progressBar.setStringPainted(false);
				progressBar.setIndeterminate(true);
			}
		});
		
		target = new JTextField();
		target.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnStart)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAbort))
						.addComponent(target, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
						.addComponent(url))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(url, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(target, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnStart)
								.addComponent(btnAbort)))
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
		);
		
		scrollPane.setViewportView(path);
		contentPane.setLayout(gl_contentPane);
		
		
	}
}
