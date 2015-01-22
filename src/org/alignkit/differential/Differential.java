package org.alignkit.differential;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alignkit.A3Reader;
import org.alignkit.Alignment;
import org.alignkit.AlignmentReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.ImageUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Differential {

	/**
	 * Custom mouse listener hack so we can highlight rows.
	 * @author Jeremy Gwinnup
	 *
	 */
	private class AlignGridMouseListener implements MouseMotionListener {
		private final Figure figure;
		boolean highlight = false; //highlight on mouseover
		int row;
		int col;

		private AlignGridMouseListener(Figure label2, int row, int col) {
			this.figure = label2;
			this.row = row;
			this.col = col;
		}
		
		private AlignGridMouseListener(Figure label2, int row, int col, boolean highlight) {
			this.figure = label2;
			this.row = row;
			this.col = col;
			this.highlight = highlight;
		}

		@Override
		public void mouseDragged(
				org.eclipse.draw2d.MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(
				org.eclipse.draw2d.MouseEvent arg0) {
			
			if(highlight)
				figure.setBackgroundColor(ColorConstants.green);
			
			//set row and column labels
			alignGridControls[row][0].setBackgroundColor(ColorConstants.green);
			
			//alignGridControls[0][col].setBackgroundColor(ColorConstants.green);
			alignGridControls[0][col].setBackgroundColor(ColorConstants.green);
			
			((ImageFigure) alignGridControls[0][col]).setImage(makeVerticalText(targetWords.get(col - 1), ColorConstants.green));
		}

		@Override
		public void mouseExited(
				org.eclipse.draw2d.MouseEvent arg0) {
			
			if(highlight)
				figure.setBackgroundColor(ColorConstants.white);
			
			alignGridControls[row][0].setBackgroundColor(ColorConstants.white);

			((ImageFigure) alignGridControls[0][col]).setImage(makeVerticalText(targetWords.get(col - 1), ColorConstants.white));

		}

		@Override
		public void mouseHover(
				org.eclipse.draw2d.MouseEvent arg0) {
			// TODO Auto-generated method stub
			//figure.setBackgroundColor(ColorConstants.blue);
			
		}

		@Override
		public void mouseMoved(
				org.eclipse.draw2d.MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}

	public static String name="Differential";
	public static String version="0.0.1";
	public static String appName = name + " " + version;

	private static Logger logger = Logger.getLogger(Differential.class);

	protected Shell differentialShell;
	private Table table;
	//private List<Alignment> list1 = null;
	//private List<Alignment> list2 = null;
	private FigureCanvas alignGrid = null;
	private Figure[][] alignGridControls = null;
	private List<String> targetWords = null;

	private static String[] alignFiles = null;
	private static String[] alignLabels = null; 
	private List<AlignSet> alignSet;
	
	public Differential() {
		//alignSet = new ArrayList<List<Alignment>>();
		//BasicConfigurator.configure();
		//logger.setLevel(Level.INFO);
	}

	/**
	 * Launch the application.
	 * Parse dat command line.
	 * @param args
	 */
	public static void main(String[] args) {
		
		BasicConfigurator.configure();
		logger.setLevel(Level.INFO);
		
		Options options = new Options();
		options.addOption("h", "help", false, "help"); //help/usage
		Option alignmentOption = new Option("a", "alignment", true, "input Alignments, comma separated [required]");
		options.addOption(alignmentOption);
		Option alignLabelOption = new Option("l", "label", true, "labels for input Alignments, comma separated [required]");
		options.addOption(alignLabelOption);
		
		// create the parser
		try {
			CommandLineParser parser = new GnuParser();

			// parse the command line arguments
			CommandLine line = parser.parse( options, args );
			
			if(line.hasOption('a')) {
				alignFiles = line.getOptionValue('a').split(",");
			}
			
			if(line.hasOption('l')) {
				alignLabels = line.getOptionValue('l').split(",");
				
				for(int i =0; i < alignLabels.length; i++) {
					logger.info("Label" + i + ": " + alignLabels[i]);
				}
			}
		}
		catch( ParseException exp ) {
			logger.error( "Parse error:" + exp.getMessage() );
			usage(options);
			return;
		}
		
		try {
			
			
			Differential window = new Differential();
			window.open(alignFiles);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Print usage
	 * @param options
	 */
	public static void usage(Options options) {

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(appName, options);
	}

	//Convenience class to make me not freak out
	public class AlignSet {
		//these necessary
		public String sourceSentence;
		public String targetSentence;
		public List<Alignment> alignments = new ArrayList<Alignment>();
	}
	
	
	/**
	 * Open the window.
	 */
	public void open(String[] alignFiles) {
		Display display = Display.getDefault();
		createContents();
		
		//load alignments if specified on the command line
		//loadAlignments(alignFiles[0]); //hax for now
		alignSet = new ArrayList<AlignSet>();
		
		boolean first = true;
		for(int i=0; i< alignFiles.length; i++){
			
			//load alignments here
			List<Alignment> thisSet = loadAlignments(alignFiles[i]);
			
			//build the structure with the first set
			if(first){
				for(Alignment a : thisSet){
					
					AlignSet cur = new AlignSet();
					cur.sourceSentence = a.getSourceWordsAsString();
					cur.targetSentence = a.getTargetWordsAsString();
					cur.alignments.add(a);
					
					alignSet.add(cur);
				}
				
				first = false;
			}
			//otherwise iterate over the existing list and append the new
			//alignment to the alignments list...
			else {
				
				Iterator<AlignSet> itr = alignSet.iterator();
				
				for(Alignment a: thisSet) {
					AlignSet cur = itr.next();
					cur.alignments.add(a);
				}
				
			}
			
		}
		loadAlignTable(alignSet);
		
		differentialShell.open();
		differentialShell.layout();
		while (!differentialShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private List<Alignment> loadAlignments(String string) {
		
		List<Alignment> ret = new ArrayList<Alignment>();
		
		logger.info("Loading alignments from: " + string);
		AlignmentReader reader;
		try {
			reader = new A3Reader(string);
			Alignment cur;
			
			while ((cur = reader.readAlignment()) != null) {
				ret.add(cur);
			}	
			
			reader.close();
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported Encoding Exception: "  + e.getLocalizedMessage());
			//e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error("File Not Found Exception: "  + e.getLocalizedMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			logger.error("IO Exception: "  + e.getLocalizedMessage());
			//e.printStackTrace();
		}
		
		return ret;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		differentialShell = new Shell();
		differentialShell.setSize(700, 459);
		differentialShell.setText("Differential");
		differentialShell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Menu menu = new Menu(differentialShell, SWT.BAR);
		differentialShell.setMenuBar(menu);

		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("File");

		Menu fileMenu = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(fileMenu);

		MenuItem mntmOpen = new MenuItem(fileMenu, SWT.NONE);
		mntmOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("File Open selected");
				OpenAlignmentsDialog dialog = new OpenAlignmentsDialog(differentialShell, SWT.NONE);
				
				List<String> result = dialog.open();
				//Load alignment sets 
				//list1 = loadAlignments(result.get(0));
				//just work with one alignment for now...
				//list2 = loadAlignments(result.get(1));
				
				//logger.info("Loaded " + list1.size() + " alignments");
			}
		});
		mntmOpen.setText("Open");

		MenuItem mntmExit = new MenuItem(fileMenu, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("File Exit selected");
				differentialShell.close();
			}
		});
		mntmExit.setText("Exit");

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		Menu helpMenu = new Menu(mntmHelp);
		mntmHelp.setMenu(helpMenu);

		MenuItem mntmAbout = new MenuItem(helpMenu, SWT.NONE);
		mntmAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Help About selected");
			}
		});
		mntmAbout.setText("About");

		SashForm sashForm = new SashForm(differentialShell, SWT.NONE);

		// bang in the GEF canvas
		alignGrid = new FigureCanvas(sashForm);
		//alignGrid.setLayout(new GridLayout());
		//fc = new FigureCanvas(alignGrid, SWT.DOUBLE_BUFFERED);
		//fc.setBackgroundColor(SWT.COLOR_BLUE);

		table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Entry Selected");
				
				clearAlignGrid();
				
				TableItem item = (TableItem) e.item;

				//peek at this data type
				List<Alignment> align = (List<Alignment>) item.getData();
				populateAlignGrid(align);
				
			}
		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		
		TableColumn idColumn = new TableColumn (table, SWT.NONE);
		idColumn.setWidth(50);
		idColumn.setText("Id");
		
		TableColumn sourceColumn = new TableColumn (table, SWT.NONE);
		sourceColumn.setWidth(100);
		sourceColumn.setText("Source");
		
		TableColumn targetColumn = new TableColumn (table, SWT.NONE);
		targetColumn.setWidth(100);
		targetColumn.setText("Target");
		
		sashForm.setWeights(new int[] {1, 1});



	}

	protected void loadAlignTable(List<AlignSet> aSet) {
		
		
		//Get iterators for all
		//List<Iterator< List<Alignment> > > = new ArrayList<Iterator<List<Alignment>>>();
		
		//Iterate over each set individually.
		
		//hmm rethink this. should be a struct matching
		//the layout of the table...
	
		for(AlignSet cur : aSet) { 		
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, Integer.toString(cur.alignments.get(0).getId()));
			item.setText(1, cur.sourceSentence);
			item.setText(2, cur.targetSentence);
			item.setData(cur.alignments); //a ha!
		}
	}
	
	void clearAlignGrid() {
		logger.info("Clearing align grid");
		//alignGrid.dispose();
		//Control[] children = alignGrid.getChildren();
		
		//for(Control c : children){
		//	c.dispose();
		//}
	}
	
	private Image makeVerticalText(String word, Color bgcolor) {
		Image icon = ImageUtilities.createRotatedImageOfString(word,
		differentialShell.getDisplay().getSystemFont(), ColorConstants.titleForeground, bgcolor);
		return icon;
	}
	
	void populateAlignGrid(List<Alignment> align) {
		
		//Font ourfont = new Font( Display.getCurrent(), "Helvetica", 14, SWT.BOLD);
		//compute dimensions
		//add 1 to each dimension for labels
		
		//source = row
		//target = column
		
		List<String> sourceWords = align.get(0).getSourceWords();
		targetWords = align.get(0).getTargetWords();
		
		//assume no layout data
		Panel baseFigure = new Panel();
		baseFigure.setBackgroundColor(ColorConstants.white);
		GridLayout layout = new GridLayout();

		layout.numColumns = targetWords.size() + 1;
		//layout.numRows = align.getTargetWords().size() + 1;
		
		//init alignGridControl container
		alignGridControls = new Figure[sourceWords.size() + 1][targetWords.size() + 1];
		
		baseFigure.setLayoutManager(layout);
		
		//create items...
		//Figure curRowLabel = null;
		//Figure curColLabel = null;
		
		for(int i=0; i < sourceWords.size() + 1; i++) {
			
			for(int j=0; j < targetWords.size() + 1; j++) {

				//column labels
				if( (i==0) && (j > 0)) {
					ImageFigure image = new ImageFigure();
					image.setOpaque(true);
					image.setImage(makeVerticalText(targetWords.get(j-1), ColorConstants.white));
					//Image icon = ImageUtilities.createRotatedImageOfString(targetWords.get(j-1),
					//differentialShell.getDisplay().getSystemFont(), ColorConstants.titleForeground, ColorConstants.white);
					//image.setImage(icon);
					baseFigure.add(image);
					
					//add to control pile
					alignGridControls[i][j] = image;
					
				}
				
				//row figure
				else if((j == 0) && (i > 0)) {					
					Label label = new Label();
					label.setText(sourceWords.get(i - 1));
					label.setOpaque(true);
					baseFigure.add(label);
					//add to control pile
					alignGridControls[i][j] = label;
				}
				else if ((j > 0) && (i > 0)){
					
					//check the alignment, mark if it's checked
					
					Figure label = null;
					
					//if(align.linkExists(i,  j)) {
					//if(linkExists(align, i, j)) {
					int numLinks = linkExists(align, i-1, j-1);
					int linkMask = linkMask(align, i-1, j-1);
					if(numLinks > 0){
						
						//Basic Twister
						//label = makeAlignPie(numLinks); //new Label();
						
						//Trivial Pursuit
						label = new PieFigure(alignLabels.length, linkMask);
						//label.setSize(30, 30);
			
						label.setOpaque(true);
						label.addMouseMotionListener(new AlignGridMouseListener(label, i, j));
					}
					else {
						label = makeBlank();
						label.addMouseMotionListener(new AlignGridMouseListener(label, i, j, true));
					}
					
					//set properties for grid
					label.setOpaque(true);
					
					label.setToolTip(makeGridToolTip(sourceWords.get(i-1), targetWords.get(j-1), align, i-1 , j-1));			
					baseFigure.add(label);
					
					//add to control pile
					alignGridControls[i][j] = label;
				}
				else {
					//this is the null figure at 0,0
					final Label label = new Label();
					label.setOpaque(true);
					baseFigure.add(label);
				}
			}
			
		}
		
		alignGrid.setContents(baseFigure);
		alignGrid.redraw();
		//ourfont.dispose(); //clean up
	}
	
	private int linkMask(List<Alignment> align, int i, int j) {
		
		int ret = 0;
		int cnt = 0;
		for(Alignment a: align) {
			if(a.linkExists(i, j)) {
				ret = ret | (int) Math.pow(2, cnt);
			}
			cnt++;
		}
		return ret;
	}

	private int linkExists(List<Alignment> align, int i, int j) {
		
		int ret = 0;
		
		for(Alignment a: align) {
			if(a.linkExists(i, j)) {
				ret++;
			}
		}
		
		return ret;
	}

	//Make this fancier
	private Figure makeGridToolTip(String sourceWord, String targetWord, List<Alignment> aligns, int i, int j) {
		Label label = new Label();
		String toolTipText = "source: " + sourceWord + "\ntarget: " + targetWord; 
		
		int cnt = 0;
		for(Alignment a : aligns) {
			if (a.linkExists(i, j)){
				toolTipText += "\n" + alignLabels[cnt];
			}
			cnt++;
		}
		label.setText(toolTipText);
		return label;
	}

	private Figure makeBlank() {
		// TODO Auto-generated method stub
		Figure f = new Figure();
		f.setSize(30, 30);
		return f;
	}

	private Figure makeAlignPie(int cnt) {
		//Canvas ret = new Canvas(differentialShell, SWT.NONE);
		//ret.setSize(30,30);
		//org.eclipse.draw2d.Ellipse();
		// TODO Auto-generated method stub
		Ellipse e = new Ellipse();
		//EllipseAnchor ea = new EllipseAnchor();
		
		//PieFigure e = new PieFigure(cnt, 0);
		
		//Arc a = new Arc();
		//org.eclipse.draw2d.
		e.setFill(true);
		switch(cnt){
		case 1:
			e.setForegroundColor(ColorConstants.red);
			e.setBackgroundColor(ColorConstants.red);
			break;
		case 2:
			e.setForegroundColor(ColorConstants.orange);
			e.setBackgroundColor(ColorConstants.orange);
			break;
		case 3:
			e.setForegroundColor(ColorConstants.green);
			e.setBackgroundColor(ColorConstants.green);
			break;
		case 4:
			e.setForegroundColor(ColorConstants.blue);
			e.setBackgroundColor(ColorConstants.blue);
			break;
		case 5:
			e.setForegroundColor(new Color(Display.getCurrent(), 128, 0, 128));
			e.setBackgroundColor(new Color(Display.getCurrent(), 128, 0, 128));
			break;
		default:
			e.setForegroundColor(ColorConstants.black);
			e.setBackgroundColor(ColorConstants.gray);
		}
		e.setSize(30, 30);
		e.setOpaque(true);
		//e.setText("" + cnt);
	
		
		return e;//ret;
	}
	
}
