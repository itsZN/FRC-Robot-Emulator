package emulator;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import edu.wpi.first.wpilibj.AnalogModule;

public class PotentiometerPart extends Part implements IMotorConnector{
	int channel = 0;
	double analogValue = 0;
	double gearRatio = 1;
	int turns = 3;
	long lastFrame = -1;
	double currentPos = 0;
	MotorConnector motorConnector = new MotorConnector(this);
	
	public PotentiometerPart(int x, int y, int width, int height) {
		super(x,y,width,height);
		name = "Potentiometer";
		props = new JComponent[5];
		String[] items = new String[AnalogModule.kAnalogChannels+1];
		items[0]="None";
		for (int i=1; i<=AnalogModule.kAnalogChannels;i++)
			items[i]=String.valueOf(i);
		JComboBox<String> channel = new JComboBox<String>(items);
		channel.setSelectedIndex(0);
		props[0] = channel;
		SpinnerNumberModel turnModel = new SpinnerNumberModel(3.0,1,20,1);
		JSpinner turns = new JSpinner(turnModel);
		props[1] = turns;
		SpinnerNumberModel gearModel = new SpinnerNumberModel(1.0,-1000,1000,.5);
		JSpinner gears = new JSpinner(gearModel);
		props[2] = gears;
		SpinnerNumberModel analogModel = new SpinnerNumberModel(0.0,-0.0001,20,.1);
		JSpinner analog = new JSpinner(analogModel);
		props[3] = analog;
		
		
	}
	
	public PotentiometerPart(int x, int y) {
		this(x,y,50,50);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawRect(x, y, width, height);
		g.setColor(Color.black);
		g.drawString("Analog Value: "+Utils.truncate(analogValue), x, y+height+11);
		g.drawString("Turns: "+Utils.truncate(currentPos), x, y+height+22);
		
		if (currentPos<0 || currentPos>turns) {
			g.setColor(Color.red);
			g.drawString("Warning! Potentiometer maxed out!",x,y+height+33);
		}
		super.paint(g);
	}
	
	@Override
	public void getProperties(JPanel p) {
		p.add(new JLabel(name));
		JPanel n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Analog Port"));
			((JComboBox)props[0]).setSelectedIndex(channel);
			n.add(props[0]);
		p.add(n);
		n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Max Number of Turns"));
			((JSpinner)props[1]).setValue(turns);
			n.add(props[1]);
		p.add(n);
		n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Gear Ratio"));
			((JSpinner)props[2]).setValue(gearRatio);
			n.add(props[2]);
		p.add(n);
		n = new JPanel();
			n.setLayout(new FlowLayout());
			n.add(new JLabel("Current Position"));
			((JSpinner)props[3]).setValue(currentPos);
			n.add(props[3]);
		p.add(n);
		super.getProperties(p);
	}
	
	@Override
	public void update() {
		super.update();
		long dif = 0;
		if (lastFrame!=-1)
			dif = System.currentTimeMillis()-lastFrame;
		lastFrame = System.currentTimeMillis();
		if (motorConnector.motorParent!=null){
			currentPos += (motorConnector.motorParent.speed * ((double)dif)/1000d * gearRatio);
		}
		analogValue = currentPos/((double)turns);
		if (analogValue>1)
			analogValue=1;
		if (analogValue<0)
			analogValue=0;
	}
	
	@Override
	public void updateProperties() {
		super.updateProperties();
		channel = ((JComboBox)props[0]).getSelectedIndex();
		turns = Double.valueOf(((JSpinner)props[1]).getValue().toString()).intValue();
		gearRatio = Double.valueOf(((JSpinner)props[2]).getValue().toString());
		currentPos = Double.valueOf(((JSpinner)props[3]).getValue().toString());
		
	}

	@Override
	public MotorConnector getMotorConnector() {
		return motorConnector;
	}

}
