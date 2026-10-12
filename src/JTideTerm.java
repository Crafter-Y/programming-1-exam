import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class JTideTerm extends JFrame {
	
	private final static String FRAME_TITLE = "JTide - Watt willste!?";

	private JComboBox<Location> cbxLoc;
	private final JRadioButton rbtnWadden = new JRadioButton("Wattwanderung");
	private final JRadioButton rbtnSwim = new JRadioButton("Schwimmen");
	private final JButton btnOk = new JButton("OK");
	private final JTideHelpers.TideMap map = new JTideHelpers.TideMap();
	private final JTideHelpers.DatePicker dateFrom = new JTideHelpers.DatePicker(false);
	private final JTideHelpers.DatePicker dateTo = new JTideHelpers.DatePicker(LocalDate.now().plusDays(1), true);
	private final JTideHelpers.InfoContainer infoContainer = new JTideHelpers.InfoContainer();
    private final List<TideValue> values;
	private Location selectedLocation;
    private Thread countdown = null;

	public JTideTerm(List<Location> locations, List<TideValue> values) {
		super(JTideTerm.FRAME_TITLE);
        this.values = values;

        if (locations.isEmpty()) {
            throw new LocationRequiredException();
        }

        this.selectedLocation = locations.getFirst();
        this.map.setMarker(this.selectedLocation.getLat(), this.selectedLocation.getLon());
		
		this.initUI(locations);
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	/**
	 * Initialize the user interface
	 * 
	 * @param locations locations to add to combo box for selection
	 */
	private void initUI(List<Location> locations) {
		this.cbxLoc = new JComboBox<>(locations != null ? locations.toArray(Location[]::new) : new Location[0]);

        this.cbxLoc.addActionListener(_ -> {
            this.selectedLocation = locations.get(this.cbxLoc.getSelectedIndex());
            this.map.setMarker(this.selectedLocation.getLat(), this.selectedLocation.getLon());
        });

		this.setLayout(new GridLayout(1, 2, 5, 5));
		
		JPanel panLeft = new JPanel(new BorderLayout(5, 5));
		
		JPanel panLeftTop = new JPanel(new BorderLayout(5, 5));
		JPanel panDateSelection = new JPanel(new GridLayout(1, 2, 5, 5));
		
		panLeft.add(panLeftTop, BorderLayout.NORTH);
		panDateSelection.add( this.dateFrom );
		panDateSelection.add( this.dateTo );
		
		ButtonGroup btnGrpActivities = new ButtonGroup();
		btnGrpActivities.add(this.rbtnWadden);
		btnGrpActivities.add(this.rbtnSwim);
		this.rbtnWadden.setSelected(true);
		
		JPanel panActivities = new JPanel();
		panActivities.add(this.rbtnWadden);
		panActivities.add(this.rbtnSwim);
		
		panLeftTop.add(this.cbxLoc, BorderLayout.NORTH);
		panLeftTop.add(panDateSelection, BorderLayout.CENTER);
		panLeftTop.add(panActivities, BorderLayout.SOUTH);

		JPanel panBtnOk = new JPanel();

		panBtnOk.add(this.btnOk);
		panLeft.add(panBtnOk, BorderLayout.SOUTH);
		
		panLeft.add(this.infoContainer, BorderLayout.CENTER);
		
		this.add(panLeft);
		this.add(this.map);

        this.btnOk.addActionListener(_ -> {
            long from = this.dateFrom.getSelectedAsTimestamp();
            long to = this.dateTo.getSelectedAsTimestamp();
            TideType type = this.rbtnWadden.isSelected() ? TideType.LOW : TideType.HIGH;

            List<TideValue> filtered = this.values.stream()
                    .filter(value -> value.getLocationId().equals(this.selectedLocation.getId()))
                    .filter(value -> value.getType() == type)
                    .filter(value -> value.getTime() >= from)
                    .filter(value -> value.getTime() <= to)
                    .toList();

            this.infoContainer.clear();
            this.infoContainer.add(selectedLocation.getName());

            for (TideValue value : filtered) {
                this.infoContainer.addEmptyLine();
                this.infoContainer.add(String.format(
                        "%s mit %.2fm um %s",
                        value.getType().getLabel(),
                        value.getValue(),
                        JTide.formatDateTime(value.getTime())
                ));
                this.infoContainer.add(String.format(
                        "%s: %s bis %s",
                        value.getType().getActivity(),
                        JTide.formatTime(value.getTime() + value.getType().getFrom()),
                        JTide.formatTime(value.getTime() + value.getType().getTo())
                ));
            }

            JTideTerm.appendLogline(String.format(
                    "%s: %s -> %s von %s bis %s",
                    JTide.formatDateTime(System.currentTimeMillis()),
                    this.selectedLocation.getName(),
                    type.getActivity(),
                    JTide.formatDate(from),
                    JTide.formatDate(to - 60*60*12) // on my system, if to is selected to 13.10., the actual timestamp points to 14.10.2026 01:59
            ));

            if (countdown != null && countdown.isAlive()) {
                countdown.interrupt();
            }
            countdown = new Thread(() -> {
                try {
                    int eraseAfter = 10;
                    while (eraseAfter > 0) {
                        this.setTitle(String.format("%s [%d]", JTideTerm.FRAME_TITLE, eraseAfter));
                        Thread.sleep(1000);
                        eraseAfter--;
                    }
                    this.setTitle(JTideTerm.FRAME_TITLE);
                    this.infoContainer.clear();
                } catch (InterruptedException _) {
                    // ignored
                }
            });
            this.countdown.start();
        });

		this.setSize(750, 500);
		this.setLocationRelativeTo(null); // center of screen
	}

    private static void appendLogline(String logline) {
        try {
            Path output = Paths.get("./infolog.txt");
            if (!Files.exists(output)) Files.createFile(output);

            Files.writeString(output, logline + "\n", StandardOpenOption.APPEND);
        } catch (IOException _) {
            // ignored
        }
    }
}
