import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Base64;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

@SuppressWarnings("serial")
public class JTideHelpers {

	/**
	 * Auto scrolling, auto top align container element, to add and clear texts. Usage:
	 * <pre>
	 * JTideHelpers.InfoContainer infoContainer = new JTideHelpers.InfoContainer();
	 * 
	 * // Add text line 
	 * infoContainer.add("Lorem ipsum dolor sit amet");
	 * // Add empty line 
	 * infoContainer.addEmptyLine();
	 * 
	 * // Remove entire text
	 * infoContainer.clear();
	 * </pre>
	 */
	public static class InfoContainer extends JScrollPane {
		
		/**
		 * Count of elements
		 */
		private int messagesCount = 0;
		
		/**
		 * Inner container panel
		 */
		private JPanel innerContainer = new JPanel(new GridBagLayout());
		
		/**
		 * Filler for y direction to ensure top alignment
		 */
		private JPanel fillerY = new JPanel();
		
		/**
		 * Create info container
		 */
		public InfoContainer() {
			this.setViewportView(this.innerContainer);
		}
		
		/**
		 * Add a text as simple line
		 * @param text text to add as line
		 */
		public void add( String text ){
			JLabel lbl = new JLabel(text);
			this.innerContainer.remove(this.fillerY);
			lbl.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
			this.innerContainer.add(lbl, new GridBagConstraints(0, this.messagesCount++, 1, 1, 1., 0., GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
			this.innerContainer.add(this.fillerY, new GridBagConstraints(0, this.messagesCount, 1, 1, 1., 1., GridBagConstraints.BASELINE_LEADING, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
			this.innerContainer.revalidate();
			
			this.add(new JLabel(text));
		}
		
		/**
		 * Add an empty line
		 */
		public void addEmptyLine(){
			this.add("");
		}
		
		/**
		 * Remove all texts
		 */
		public void clear(){
			this.innerContainer.removeAll();
			this.messagesCount = 0;
			this.innerContainer.revalidate();
			this.innerContainer.repaint();
		}
	}
	
	/**
	 * Map component
	 */
	public static class TideMap extends JComponent {
		
		/**
		 * The background map image, initialized once
		 */
		private final static BufferedImage MAP_IMAGE = TideMap.getMap();
		
		/**
		 * East edge longitude of the map image
		 */
		private final static double EDGE_EAST = 9.2945355;

		/**
		 * West edge longitude of the map image
		 */
		private final static double EDGE_WEST = 7.1131673;

		/**
		 * North edge latitude of the map image
		 */
		private final static double EDGE_NORTH = 55.0697589;

		/**
		 * South edge latitude of the map image
		 */
		private final static double EDGE_SOUTH = 53.2857027;

		/**
		 * Longitude range from west to east
		 */
		private final static double RANGE_X = TideMap.EDGE_EAST - TideMap.EDGE_WEST;
		
		/**
		 * Latitude range from north to south
		 */
		private final static double RANGE_Y = TideMap.EDGE_NORTH - TideMap.EDGE_SOUTH;

		/**
		 * Marker radius
		 */
		private final static int RADIUS_MARKER = 8;
		
		/**
		 * Current map with marker painted on it
		 */
		private BufferedImage mapWithMarker;
		
		/**
		 * Set marker to provided latitude and longitude
		 * 
		 * @param lat latitude to draw marker at
		 * @param lon longitude to draw marker at
		 */
		public void setMarker( double lat, double lon ) {
			if ( lon > lat ) {
				// Warn but no correct wrong order for lat/lon in western Europe
				System.err.println("WARNING: Latitude and longitude probably provided in wrong order!");
			}
			// Quick-and-dirty linear interpolation - sufficient for this use case.
			// (real geographic operations way out of scope here ;-)
			int markerX = (int)Math.round(((lon - TideMap.EDGE_WEST) / TideMap.RANGE_X) * MAP_IMAGE.getWidth()) - RADIUS_MARKER;
			int markerY = MAP_IMAGE.getHeight() - (int)Math.round(((lat - TideMap.EDGE_SOUTH) / TideMap.RANGE_Y) * MAP_IMAGE.getHeight()) - RADIUS_MARKER;
			
			this.mapWithMarker = new BufferedImage(MAP_IMAGE.getWidth(), MAP_IMAGE.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			
			Graphics2D g2 = this.mapWithMarker.createGraphics();
			g2.drawImage(MAP_IMAGE, null, 0, 0);
			g2.setColor(Color.RED);
			g2.fillOval(markerX, markerY,RADIUS_MARKER*2,RADIUS_MARKER*2);
			g2.dispose();
			
			this.repaint();
		}
		
		/**
		 * Draw the map onto component ('contain' resize)
		 */
		@Override
		protected void paintComponent(Graphics g) {
		    super.paintComponent(g);

		    BufferedImage bi = this.mapWithMarker != null ? this.mapWithMarker : MAP_IMAGE;

		    Graphics2D g2 = (Graphics2D) g.create();

		    int compWidth = getWidth();
		    int compHeight = getHeight();

		    int imgWidth = bi.getWidth();
		    int imgHeight = bi.getHeight();

		    double scale = Math.min((double) compWidth / imgWidth,(double) compHeight / imgHeight);

		    int drawWidth = (int) Math.round(imgWidth * scale);
		    int drawHeight = (int) Math.round(imgHeight * scale);

		    int x = (compWidth - drawWidth) / 2;
		    int y = (compHeight - drawHeight) / 2;

		    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		    g2.drawImage(bi, x, y, drawWidth, drawHeight, null);

		    g2.dispose();
		}
		
		/**
		 * Background image map from base64 data
		 * @return background image map
		 */
		private static BufferedImage getMap() {
			try ( ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode("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"))){
				return ImageIO.read(bais);
			} catch (Exception e) {
				// On any error: blank image of same size
				return new BufferedImage(561, 692, BufferedImage.TYPE_3BYTE_BGR);
			}
			
		}
		
	}
	
	/**
	 * Date picker
	 */
	public static class DatePicker extends JComponent {
		
		/**
		 * Formatter for text field with day name (short) and date
		 */
		private static DateTimeFormatter TEXTFIELD_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy", Locale.getDefault());
		
		/**
		 * Provide {@link #getSelectedAsTimestamp() time stamp} at end of the day
		 */
		private final boolean endOfDay;
		
		/**
		 * Currently selected date
		 */
		private LocalDate selected;
		
		/**
		 * Textfield for display of current date
		 */
		private JTextField txtDisplay = new JTextField();
		
		/**
		 * Initialize the date picker
		 * @param endOfDay flag to indicate if {@link #getSelectedAsTimestamp() time stamp} shall be provided at the end of the day (start of day otherwise)
		 */
		public DatePicker(boolean endOfDay) {
			this(LocalDate.now(),endOfDay);
		}
		
		/**
		 * Initialize the date picker
		 * @param initDate initial date
		 * @param endOfDay flag to indicate if {@link #getSelectedAsTimestamp() time stamp} shall be provided at the end of the day (start of day otherwise)
		 */
		public DatePicker(LocalDate initDate, boolean endOfDay) {
			this.endOfDay = endOfDay;

			this.initUI();
			this.setSelected(initDate != null ? initDate : LocalDate.now());
		}
		
		/**
		 * Set the selected date (from dialog, no public api)
		 * @param d date to set
		 */
		private void setSelected( LocalDate d ) {
			this.selected = d;
			this.txtDisplay.setText(DatePicker.TEXTFIELD_FORMATTER.format(d));
		}
		
		/**
		 * Initialize the user interface
		 */
		private void initUI() {
			this.setLayout(new BorderLayout(5, 5));
			this.txtDisplay.setEditable(false);
			this.txtDisplay.setHorizontalAlignment(SwingConstants.RIGHT);
			
			JButton btn = new JButton("...");
			this.add(this.txtDisplay, BorderLayout.CENTER);
			this.add(btn, BorderLayout.EAST);
			
			btn.addActionListener(e->{
				JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
				JDialog popup = new JDialog(owner, true);
				popup.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				popup.setResizable(false);
				popup.setUndecorated(true);
				
				
				popup.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closePopup");
				popup.getRootPane().getActionMap().put("closePopup", new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						popup.dispose();
					}
				});
				
				DatePickerDialogContent content = new DatePickerDialogContent(this.selected);
				popup.add(content);
				
				content.addContentControlsListener(new DatePickerDialogContent.ContentControlsListener() {
					@Override
					public void ok(LocalDate val) {
						DatePicker.this.setSelected(val);
						popup.dispose();
					}
					
					@Override
					public void cancel() {
						popup.dispose();
					}
				});
				popup.pack();
				Point btnLocationOnScreen = btn.getLocationOnScreen();
				popup.setLocation(btnLocationOnScreen.x, btnLocationOnScreen.y + btn.getHeight());
				popup.setVisible(true);
			});
		}
		
		/**
		 * Get the selected date as time stamp. Will honor end of day flag provided at init.
		 * @return selected date as time stamp
		 */
		public long getSelectedAsTimestamp() {
			return this.selected.toEpochSecond(this.endOfDay ? LocalTime.MAX : LocalTime.MIDNIGHT, ZoneOffset.UTC);
		}
		
		/**
		 * Content for the popup
		 */
		private static class DatePickerDialogContent extends JComponent {
			/**
			 * Background color for selected date
			 */
			private final static Color SELECTED_BG = new Color(254,254,233);
			
			/**
			 * Formatter for month at top of dialog (e.g.a: April 2042)
			 */
			private static DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
			
			/**
			 * {@link JButton} with storage for date and auto label + tooltip update
			 */
			private static class DateButton extends JButton {
				/**
				 * Tooltip date formatter with day number and month
				 */
				private static DateTimeFormatter TOOLTIP_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault());
				
				/**
				 * Date of button
				 */
				private LocalDate date;
				
				/**
				 * Set the date of the button
				 * @param date date to set
				 */
				public void setDate(LocalDate date) {
					this.date = date;
					if ( this.date != null ) {
						this.setText(Integer.toString(date.get(ChronoField.DAY_OF_MONTH)));
						this.setToolTipText(DateButton.TOOLTIP_FORMATTER.format(date));
					} else {
						this.setText("");
						this.setToolTipText(null);
					}
				}
				
				/**
				 * Get the date
				 * @return date
				 */
				public LocalDate getDate() {
					return this.date;
				}
			}
			
			/**
			 * Listener if controls where used from dialog
			 */
			public interface ContentControlsListener extends EventListener {
				/**
				 * OK Button pressed
				 * @param val current selection value
				 */
				void ok ( LocalDate val );
				/**
				 * Cancel button pressed
				 */
				void cancel ();
			}

			/**
			 * Event listener list
			 */
			private final EventListenerList listenerList = new EventListenerList();
	
			/**
			 * Currenlty selected date
			 */
			private LocalDate currentSelection;
			
			/**
			 * Currently show month
			 */
			private LocalDate currentShownMonth;
			
			/**
			 * All date buttons (6 rows, 7 days => 42 buttons)
			 */
			private final List<DateButton> dateButtons = new LinkedList<>();
			
			/**
			 * Label on top for current month
			 */
			private final JLabel lblCurrShownMonth = new JLabel("", JLabel.CENTER);
			
			/**
			 * Create the content for the dialog
			 * @param initDate initial date value
			 */
			public DatePickerDialogContent(LocalDate initDate) {
				this.currentSelection = initDate;
				this.currentShownMonth = initDate.withDayOfMonth(1);
				this.initUI();
				
				// updater handles init as well
				this.handleDateUpdate();
			}
			
			/**
			 * Add listener for controls actions
			 * @param l listener to add
			 */
			public void addContentControlsListener(ContentControlsListener l) {
				this.listenerList.add(ContentControlsListener.class, l);
			}
			
			/**
			 * Handle update of selected date or current month
			 */
			private void handleDateUpdate() {
				String monthLabel = DatePickerDialogContent.MONTH_FORMATTER.format(this.currentShownMonth);
				String currentMonthLbl = this.lblCurrShownMonth.getText();
				// not really pretty as change detection, but works :-D
				if ( !monthLabel.equals(currentMonthLbl)) {
					this.lblCurrShownMonth.setText(monthLabel);
					this.updateButtons(true);
				} else {
					this.updateButtons(false);
				}
			}
			
			/**
			 * Update buttons
			 * @param withMonthStructure update font and days as well (other month), not just BG
			 */
			private void updateButtons(boolean withMonthStructure) {
				LocalDate firstToShow = this.currentShownMonth.withDayOfMonth(1); // just to be sure
				YearMonth monthToShow = YearMonth.from(firstToShow);
				
				DayOfWeek day = firstToShow.getDayOfWeek();
				if ( day != DayOfWeek.MONDAY ) {
					firstToShow = firstToShow.minusDays(day.ordinal()); // Mo is 0, so a Tue will get 1  and -1 is perfect.
				}
				LocalDate btnDate = firstToShow;
				for ( DateButton db : this.dateButtons ) {
					if ( withMonthStructure ) {
						db.setDate(btnDate);
						
						boolean sameMonth = YearMonth.from(btnDate).equals(monthToShow);
						db.setFont(db.getFont().deriveFont(sameMonth ? Font.BOLD : Font.PLAIN|Font.ITALIC));
					}
					db.setBackground( btnDate.equals(this.currentSelection) ? DatePickerDialogContent.SELECTED_BG : null);
					btnDate = btnDate.plusDays(1);
				}
			}
			
			/**
			 * Initialize the user interface
			 */
			private void initUI() {
				this.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Color.BLACK),
						BorderFactory.createEmptyBorder(10, 10, 10, 10)));

				this.setLayout(new BorderLayout(5, 5));
				
				JPanel panTop = new JPanel(new BorderLayout(5, 5));
				JButton btnPrevMonth = new JButton("\u25C0");
				JButton btnNextMonth = new JButton("\u25B6");
				panTop.add(btnPrevMonth,BorderLayout.WEST);
				panTop.add(lblCurrShownMonth,BorderLayout.CENTER);
				panTop.add(btnNextMonth,BorderLayout.EAST);
				
				btnPrevMonth.addActionListener(e->{
					this.currentShownMonth = this.currentShownMonth.withDayOfMonth(1).minusMonths(1);
					this.handleDateUpdate();
				});
				btnNextMonth.addActionListener(e->{
					this.currentShownMonth = this.currentShownMonth.withDayOfMonth(1).plusMonths(1);
					this.handleDateUpdate();
				});
				
				// buttons for date selection
				JPanel panDateButtons = new JPanel(new GridLayout(7, 7, 2, 2));
		        for (DayOfWeek day : DayOfWeek.values()) {
		        	panDateButtons.add(new JLabel(day.getDisplayName(TextStyle.SHORT, Locale.getDefault()), JLabel.CENTER));
		        }
		        for ( int i = 0; i < 42; i++ ) {
		        	DateButton b = new DateButton();
		        	b.addActionListener(e->{
		        		if ( b.getDate() != null ) {
		        			this.currentSelection = b.getDate();
		        			this.handleDateUpdate();
		        		}
		        	});
		        	this.dateButtons.add(b);
		        	panDateButtons.add(b);
		        }
		        
		        JPanel panCtrlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER));
		        JButton btnOk = new JButton("\u2705");
		        JButton btnCancel = new JButton("\u274C");
		        btnOk.addActionListener(e-> {
		        	for ( ContentControlsListener l : this.listenerList.getListeners(ContentControlsListener.class) ) {
		        		l.ok(this.currentSelection);
		        	}
		        });
		        btnCancel.addActionListener(e->{
		        	for ( ContentControlsListener l : this.listenerList.getListeners(ContentControlsListener.class) ) {
		        		l.cancel();
		        	}
		        });
		        panCtrlBtns.add(btnOk);
		        panCtrlBtns.add(btnCancel);
		        
				
				this.add(panTop, BorderLayout.NORTH);
				this.add(panDateButtons, BorderLayout.CENTER);
				this.add(panCtrlBtns, BorderLayout.SOUTH);
				
			}
		}
	}
	
}
