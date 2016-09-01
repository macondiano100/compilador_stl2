package visual;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mozilla.universalchardet.UniversalDetector;
public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private RSyntaxTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(
					        UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}
				try {
					MainWindow frame = new MainWindow();
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
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 707, 412);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		
		JButton btnNuevo = new JButton("Nuevo");
		btnNuevo.setEnabled(false);
		btnNuevo.addActionListener(this::AccionAbrirArchivo);
		
		JButton btnAbrir = new JButton("Abrir");
		btnAbrir.addActionListener(this::AccionAbrirArchivo);
		btnAbrir.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Directory.gif")));
		toolBar.add(btnAbrir);
		btnNuevo.setToolTipText("Abrir...");
		btnNuevo.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/File.gif")));
		toolBar.add(btnNuevo);
		
		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/FloppyDrive.gif")));
		btnGuardar.setToolTipText("Guardar");
		toolBar.add(btnGuardar);
		
		JButton btnGuardarComo = new JButton("Guardar como ....");
		btnGuardarComo.setIcon(new ImageIcon(MainWindow.class.getResource("/javax/swing/plaf/metal/icons/ocean/floppy.gif")));
		btnGuardarComo.setToolTipText("Guardar como...");
		toolBar.add(btnGuardarComo);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		textArea = new RSyntaxTextArea();
		textArea.setPopupMenu(null);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		tabbedPane.addTab("New tab", null, sp, null);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.SOUTH);
	}
	
	
	
	
	//Acciones
	private void AccionAbrirArchivo(ActionEvent e)
	{
		JFileChooser file_chooser = new JFileChooser();
		file_chooser.showOpenDialog(this);
		File selected_file =file_chooser.getSelectedFile();
		if(selected_file != null){
			UniversalDetector detector= new UniversalDetector(null);
			try(FileInputStream fis= new FileInputStream(selected_file))
			{
				int nread;
				byte[] buf = new byte[4096];
				while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				  detector.handleData(buf, 0, nread);
				}
				detector.dataEnd();
			} catch (IOException e3 ) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			String charsetName = detector.getDetectedCharset()==null ?  "UTF-8":detector.getDetectedCharset();
			try(BufferedReader reader=Files.newBufferedReader(selected_file.toPath(),Charset.forName(charsetName)))
			{
				textArea.read(reader,selected_file);
			}
			catch (MalformedInputException e2) {
				JOptionPane.showMessageDialog(this,"Error de encoding","Error al Abrir Archivo",JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
	}
	
}
