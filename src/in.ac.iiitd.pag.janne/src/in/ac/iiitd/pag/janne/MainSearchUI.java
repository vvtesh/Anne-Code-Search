package in.ac.iiitd.pag.janne;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * Tool to search within code and comments. 
 * TAs may use this tool to stamp feedback on assignments.
 * Reads all Java, C and Cpp files. Allows editing them and saving them.
 * Performs a global search across files for keywords.
 * 
 * @author Venkatesh
 *
 */
public class MainSearchUI {
	
	private static String FilePath = "C:\\temp\\workdir\\anne201602\\code\\tagged-output\\";
	private static int TOTAL_FILES = 0;
	private static String currentOpenFile = "";
	private static JTextField queryText = new JTextField(20);
	private static JTextArea editorTextArea = null;
	private static JTree globalSearchTree = null;
	private static JTree globalNegativeSearchTree = null;
	private static JFrame rootFrame = new JFrame("Code Search Tool");
	private static JScrollPane scrollEditorArea = null;
	private static JScrollPane scrollFilterArea = null;
	private static JLabel statusLabel = new JLabel("status");
	
	/**
	 * Prepare the main UI panel.
	 * 
	 */
	public static void main(String[] args) {
		
		  rootFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent e) {
	            System.exit(0);
	         }
	      });
	      
	      JPanel content = new JPanel();
	      content.setLayout(new BorderLayout());	      
	      
	      addTree(content, BorderLayout.WEST);
	      addTextArea(content);
	      addFilterTree(content);
	      addStatusBar(content);	      
	      
	      rootFrame.setContentPane(content);
	      rootFrame.pack();
	      rootFrame.setSize(new Dimension(1200, 706));
	      rootFrame.setVisible(true);
	      rootFrame.setResizable(false);
	      
	      KeyStroke keyStrokeToFind
		    = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
	        Action actionFind = new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					queryText.grabFocus();
					queryText.requestFocus();
				}
			};
	        // configure the Action with the accelerator (aka: short cut)
			actionFind.putValue(Action.ACCELERATOR_KEY, keyStrokeToFind);        
			content.getActionMap().put("findAction", actionFind);
			content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeToFind, "findAction");

	}

	private static void addStatusBar(JPanel content) {
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		content.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(content.getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
	}
	
	private static void setStatusMessage(String message) {
		statusLabel.setText(message);
	}

	/**
	 * Feature to search a single file's contents. Highlights 
	 * searched items.
	 * @param content
	 */
	private static void addSearchBar(JPanel content) {
		JPanel searchPanel = new JPanel();
		
		JButton saveButton = new JButton();
        saveButton.setText("Save");
        
        KeyStroke keyStrokeToOpen
	    = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        Action action = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setStatusMessage("Saving");
				saveToFile();
			}
		};
        // configure the Action with the accelerator (aka: short cut)
        action.putValue(Action.ACCELERATOR_KEY, keyStrokeToOpen);        
        saveButton.getActionMap().put("saveAction", action);
        saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeToOpen, "saveAction");
        
        
        
        saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveToFile();
			}
		});
        saveButton.setDisplayedMnemonicIndex(0);
        searchPanel.add(saveButton);
        
		JLabel searchLabel = new JLabel();
		searchLabel.setText("Find in file: ");
		searchLabel.setVisible(true);
		
		searchPanel.add(searchLabel);
		searchPanel.add(queryText);
		
		JButton button = new JButton("Find");
		button.setDisplayedMnemonicIndex(0);
		
        
		searchPanel.add(button);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeHighlights(editorTextArea);
				if (queryText.getText().trim().length() > 0)
					highlight(editorTextArea, queryText.getText().trim());				
			}
		});
		
		content.add(searchPanel, BorderLayout.NORTH);
	}
	
	protected static void saveToFile() {
		String text = editorTextArea.getText();
		String filePath = currentOpenFile;
		if (filePath.trim().length() == 0) return;
		File file = new File(filePath);
		if (!file.exists()) return;
		
		BufferedWriter  writer = null;

		try {
		    writer = new BufferedWriter(new FileWriter(file));
		    writer.write(text);
		    setStatusMessage("Saved.");
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
	}

	/**
	 * Clear all highlights from the text area.
	 * 
	 * @param textComp
	 */
	public static void removeHighlights(JTextComponent textComp) {
	    Highlighter hilite = textComp.getHighlighter();
	    Highlighter.Highlight[] hilites = hilite.getHighlights();

	    for (int i = 0; i < hilites.length; i++) {
	      if (hilites[i].getPainter() instanceof DefaultHighlightPainter) {
	        hilite.removeHighlight(hilites[i]);
	      }
	    }
	  }

	/**
	 * Adds a text area to given panel.
	 * 
	 * @param rootPanel
	 */
	private static void addTextArea(JPanel rootPanel) {
		JPanel panel = new JPanel();
		editorTextArea = new JTextArea(36,56);
		editorTextArea.setEditable(true);		
		
		scrollEditorArea = new JScrollPane(editorTextArea);
		panel.add(scrollEditorArea);
		TitledBorder title;
        title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Source Code Editor");
        panel.setBorder(title);
        addSearchBar(panel);
		rootPanel.add(panel, BorderLayout.CENTER);
	}

	/**
	 * Add a jTree to given panel at specified borderlayout 
	 * location.
	 * 
	 * @param rootPanel
	 * @param location
	 */
	private static void addTree(JPanel rootPanel, String location) {
		
		JPanel panel = new JPanel();
		
		JButton folderButton = new JButton("Choose Folder");
		panel.add(folderButton);
		DefaultMutableTreeNode root = loadTree(false);
		JTree tree = new JTree(root);
		tree.setEnabled(false);
		folderButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser;
				chooser = new JFileChooser(); 
			    chooser.setCurrentDirectory(new java.io.File("."));
			    chooser.setDialogTitle("Select source files");
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    
			    chooser.setAcceptAllFileFilterUsed(false);
			       
			    if (chooser.showOpenDialog(panel.getRootPane()) == JFileChooser.APPROVE_OPTION) { 
			    	FilePath = chooser.getSelectedFile().toString();
			    	TreeHandler.removeAllNodes(tree);
			    	TreeHandler.removeAllNodes(globalSearchTree);
			    	TreeHandler.removeAllNodes(globalNegativeSearchTree);
			    	
			    	File file = new File(FilePath);
			    	List<String> names = new ArrayList<String>();
			    	File[] contents = FileUtil.listSourceFiles(file);
			    	TOTAL_FILES = contents.length;
					for(File content: contents) {
						names.add(content.getName());					
					}
					TreeHandler.addNodes(tree, FilePath, names);
					//TreeHandler.addNodes(globalSearchTree, FilePath, names);
					//TreeHandler.addNodes(globalNegativeSearchTree, FilePath, new ArrayList<String>());
			      }		
			    tree.expandRow(0);
			    //globalSearchTree.expandRow(0);
			    //globalNegativeSearchTree.expandRow(0);
			    
			}
		});
		panel.setPreferredSize(new Dimension(240,540));

		tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				
				if (!FileUtil.isValidFileToOpenInEditor(e.getPath().getLastPathComponent().toString())) return;
				String path = FilePath + File.separator + e.getPath().getLastPathComponent().toString();
				currentOpenFile = path;
				setStatusMessage("");
				File file = new File(path);
				String text = "";
				try {
					text = new String(Files.readAllBytes(Paths.get(path)));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				editorTextArea.setText(text);
				editorTextArea.setCaretPosition(0);
				scrollEditorArea.getVerticalScrollBar().setValue(0);
				
			}

			
		});
        JScrollPane scrollTreeAllFiles = new JScrollPane(tree);
        scrollTreeAllFiles.setPreferredSize(new Dimension(220, 594));
        panel.add(scrollTreeAllFiles);        
        TitledBorder title;
        title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Package Explorer");
        panel.setBorder(title);
        rootPanel.add(panel, location);
	}
	
	/**
	 * A tree to show search results across all files
	 * in a given folder.
	 * 
	 * @param rootPanel
	 */
	private static void addFilterTree(JPanel rootPanel) {
		
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new FlowLayout());
		
		TitledBorder title;
        title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Filter Across Files");
        filterPanel.setBorder(title);
        
		
		JPanel searchPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel treePanel = new JPanel();
		JPanel labelFoundInPanel = new JPanel();
		JPanel labelNotFoundInPanel = new JPanel();
		JPanel negativeTreePanel = new JPanel();
		
		filterPanel.setPreferredSize(new Dimension(300, 650));
		
		centerPanel.setPreferredSize(new Dimension(250, 40));
		//treePanel.setPreferredSize(new Dimension(100,40));
		
		JTextField queryText = new JTextField(25);
		
		searchPanel.add(queryText);
		
		JButton button = new JButton("Filter");
		JLabel labelFoundIn = new JLabel("Found in: ");
		labelFoundIn.setPreferredSize(new Dimension(250,20));
		labelFoundInPanel.add(labelFoundIn);
		
		JLabel labelNotFoundIn = new JLabel("Not Found in: ");
		labelNotFoundIn.setPreferredSize(new Dimension(250,20));
		labelNotFoundInPanel.add(labelNotFoundIn);
		
		centerPanel.add(button);
		
		filterPanel.add(searchPanel);		
		filterPanel.add(centerPanel);
		filterPanel.add(labelFoundInPanel);
		DefaultMutableTreeNode root = loadTree(true);
		String rootNegativeText = StringUtil.processRootText(FilePath);
		DefaultMutableTreeNode rootNegative = new DefaultMutableTreeNode(rootNegativeText);
		
        globalSearchTree = new JTree(root);
        globalSearchTree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        globalSearchTree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (!FileUtil.isValidFileToOpenInEditor(e.getPath().getLastPathComponent().toString())) return;
				
				String path = FilePath + File.separator + e.getPath().getLastPathComponent().toString();
				currentOpenFile = path;
				setStatusMessage("");
				File file = new File(path);
				String text = "";
				try {
					text = new String(Files.readAllBytes(Paths.get(path)));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				editorTextArea.setText(text);
				editorTextArea.setCaretPosition(0);
				scrollEditorArea.getVerticalScrollBar().setValue(0);
				String[] words = queryText.getText().trim().split(" ");
				for(String word: words) {
					highlight(editorTextArea, word);
				}				
			}
		});
        JScrollPane scrollTreeAllFiles = new JScrollPane(globalSearchTree);
        //treePanel.add(labelFoundIn);
        treePanel.add(scrollTreeAllFiles); 
        
        //Negative search tree
        globalNegativeSearchTree = new JTree(rootNegative);
        globalNegativeSearchTree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        globalNegativeSearchTree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (!FileUtil.isValidFileToOpenInEditor(e.getPath().getLastPathComponent().toString())) return;
				
				String path = FilePath + File.separator + e.getPath().getLastPathComponent().toString();
				currentOpenFile = path;
				setStatusMessage("");
				File file = new File(path);
				String text = "";
				try {
					text = new String(Files.readAllBytes(Paths.get(path)));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				editorTextArea.setText(text);
				editorTextArea.setCaretPosition(0);
				scrollEditorArea.getVerticalScrollBar().setValue(0);
				String[] words = queryText.getText().trim().split(" ");
				for(String word: words) {
					highlight(editorTextArea, word);
				}				
			}
		});
        JScrollPane scrollNegativeTreeAllFiles = new JScrollPane(globalNegativeSearchTree);
        //treePanel.add(labelFoundIn);
        negativeTreePanel.add(scrollNegativeTreeAllFiles); 
        
        
		filterPanel.add(treePanel);
		filterPanel.add(labelNotFoundInPanel);
		filterPanel.add(scrollNegativeTreeAllFiles);
		scrollTreeAllFiles.setPreferredSize(new Dimension(250, 237));
		scrollNegativeTreeAllFiles.setPreferredSize(new Dimension(250, 221));
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				TreeHandler.removeAllNodes(globalSearchTree);
				TreeHandler.removeAllNodes(globalNegativeSearchTree);
				
				String query = queryText.getText().trim();
				List<String> names = new ArrayList<String>();
				File file = new File(FilePath);
		    	
		    	File[] contents = FileUtil.listSourceFiles(file); 
				for(File content: contents) {
					names.add(content.getName());					
				}	
				if (query.length() == 0) {
									
					TreeHandler.addNodes(globalSearchTree, FilePath, names);
					TreeHandler.addNodes(globalNegativeSearchTree, FilePath, new ArrayList<String>());
					return;
				}
				
				
				if (query.length() == 0) return;
				
				String[] words = query.split(" "); //Stemmer.processQuery(query); //Query terms.
				//String[] filteredFileNames = SearchHandler.searchANDonFullFile(words, FilePath);
				String[] filteredFileNames = SearchHandler.searchANDonLines(words, FilePath);
				
				if (filteredFileNames.length != contents.length) {
					for (String fileName: filteredFileNames) {
						
							DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(fileName);
							root.add(contentNode);
						
					}
					globalSearchTree.updateUI();
					globalSearchTree.expandRow(0);
					for (String name: names) {
						if (!found(name, filteredFileNames)) {
							DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(name);
							rootNegative.add(contentNode);
						}
					}
					globalNegativeSearchTree.expandRow(0);
					globalNegativeSearchTree.updateUI();
				} else {
					statusLabel.setText("Sorry, all files matched the keywords.");
				}
			}

			private boolean found(String name, String[] filteredFileNames) {
				for(String filteredFileName: filteredFileNames) {
					if (filteredFileName.equalsIgnoreCase(name)) return true;
				}
				return false;
			}
		});
		
		rootPanel.add(filterPanel, BorderLayout.EAST);		     
	}

	/**
	 * Create tree nodes from given filepath.
	 * 
	 * @return
	 */
	private static DefaultMutableTreeNode loadTree(boolean isEmpty) {
		String rootText = StringUtil.processRootText(FilePath);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootText);
		File file = new File(FilePath);
		File[] contents = FileUtil.listSourceFiles(file);
		if (isEmpty) return root;
		for(File content: contents) {
			DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(content.getName());
			root.add(contentNode);
		}		
		return root;
		
	}
	
	/**
	 * Utility method to highlight given text in a textarea.
	 * @param textComp
	 * @param pattern
	 */
	public static void highlight(JTextComponent textComp, String pattern) {

	    try {
	    	if (pattern.trim().length() == 0) return;
	    	DefaultHighlightPainter painter2 = new DefaultHighlightPainter(new Color(176,196,222));
	        Highlighter hilite = textComp.getHighlighter();
	        javax.swing.text.Document doc = textComp.getDocument();
	        String text = doc.getText(0, doc.getLength());
	        int pos = 0;

	        while ((pos = text.toLowerCase().indexOf(pattern.toLowerCase(), pos)) >= 0) {
	            hilite.addHighlight(pos, pos + pattern.length(), painter2);
	            pos += pattern.length();
	        }
	    } catch (BadLocationException e) {
	    }
	}
}
