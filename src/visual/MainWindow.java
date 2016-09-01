package visual;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

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
import javax.swing.text.JTextComponent;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mozilla.universalchardet.UniversalDetector;
public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTabbedPane tabbedPane;

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
		Controlador_pestanias_archivos fileActions = new Controlador_pestanias_archivos();
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
		
		JButton btnAbrir = new JButton("Abrir");
		btnAbrir.addActionListener(fileActions::accion_abrir_archivo);
		btnAbrir.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Directory.gif")));
		toolBar.add(btnAbrir);
		btnNuevo.setToolTipText("Abrir...");
		btnNuevo.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/File.gif")));
		btnNuevo.addActionListener(fileActions::accion_abrir_archivo);
		toolBar.add(btnNuevo);
		
		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.addActionListener(fileActions::accion_guardar_archivo);
		btnGuardar.setIcon(new ImageIcon(MainWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/FloppyDrive.gif")));
		btnGuardar.setToolTipText("Guardar");
		toolBar.add(btnGuardar);
		
		JButton btnGuardarComo = new JButton("Guardar como ....");
		btnGuardarComo.addActionListener(fileActions::accion_guardar_como);
		btnGuardarComo.setIcon(new ImageIcon(MainWindow.class.getResource("/javax/swing/plaf/metal/icons/ocean/floppy.gif")));
		btnGuardarComo.setToolTipText("Guardar como...");
		toolBar.add(btnGuardarComo);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
				
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.SOUTH);
	}
	
	
	
	
	//Acciones
	private class Controlador_pestanias_archivos
	{
		private HashMap<Path,RTextScrollPane> file_to_component=new HashMap<>();
		private HashMap<RTextScrollPane,Path> component_to_file=new HashMap<>();
		private void add_opened_file(File f)
		{
						
			UniversalDetector detector= new UniversalDetector(null);			
			try(FileInputStream fis= new FileInputStream(f))
			{
				int nread;
				byte[] buf = new byte[4096];
				while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				  detector.handleData(buf, 0, nread);
				}
				detector.dataEnd();
			} catch (IOException e3 ) {
				e3.printStackTrace();
			}
			String charsetName = detector.getDetectedCharset()==null ?  "UTF-8":detector.getDetectedCharset();
			try(BufferedReader reader=Files.newBufferedReader(f.toPath(),Charset.forName(charsetName)))
			{
				Path p = f.toPath();
				RSyntaxTextArea textArea = new RSyntaxTextArea();
				textArea.setPopupMenu(null);
				RTextScrollPane sp = new RTextScrollPane(textArea);
				file_to_component.put(p, sp);
				component_to_file.put(sp, p);
				tabbedPane.addTab(p.getFileName().toString(),sp);
				textArea.read(reader,f);
			}
			catch (MalformedInputException e2) {
				JOptionPane.showMessageDialog(MainWindow.this,"Error de encoding","Error al Abrir Archivo",JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		private void modifica_pestana_archivo(Path old_path,Path new_path)
		{
			file_to_component.put(new_path,file_to_component.get(old_path));
			file_to_component.remove(old_path);
			tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(),new_path.getFileName().toString());
		}
		public void accion_abrir_archivo(ActionEvent e)
		{
			JFileChooser file_chooser = new JFileChooser();
			file_chooser.showOpenDialog(MainWindow.this);
			File selected_file =file_chooser.getSelectedFile();
			if(selected_file != null){
				add_opened_file(selected_file);
				
			}
		}
		public void accion_guardar_como(ActionEvent e)
		{
			if(tabbedPane.getSelectedIndex()>=0) guardar_como();
		}
		private void guardar_como()
		{
			JFileChooser fileChooser = new JFileChooser();
			if(fileChooser.showSaveDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION)
			{
				Path path=fileChooser.getSelectedFile().toPath();
				if(!Files.exists(path) || 
						JOptionPane.showConfirmDialog(MainWindow.this, "El archivo "+path.getFileName()+" ya existe. ¿Sobreescribir?") == JOptionPane.OK_OPTION)
				{
					try(Writer writer = Files.newBufferedWriter(path))
					{
						JScrollPane current_component = (JScrollPane)tabbedPane.getSelectedComponent();
						((JTextComponent)current_component.getViewport().getView()).write(writer);
						modifica_pestana_archivo(component_to_file.get(current_component),path);					
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MainWindow.this,"Error de guardado","Error al Guardar Archivo",JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			}
		}

		/**/
		public void accion_guardar_archivo(ActionEvent e)
		{
			if(tabbedPane.getSelectedIndex()>=0)
			{
				JScrollPane scrollPane =(JScrollPane) tabbedPane.getSelectedComponent();
				if(component_to_file.containsKey(scrollPane))
				{
					try(Writer writer = Files.newBufferedWriter(component_to_file.get(scrollPane)))
					{
						((JTextComponent)scrollPane.getViewport().getView()).write(writer);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MainWindow.this,"Error de guardado","Error al Guardar Archivo",JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
				else
				{
					guardar_como();
				}
			}
		}
	}
	
}
