package router;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jnetpcap.PcapIf;

import router.BaseLayer.ARP_CACHE;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ApplicationLayer extends BaseLayer{
	private JTable routingTable;

	private JTable arpCacheTable;

	public void CreateWindow()
	{
		final int windowWidth = 960;
		final int windowHeight = 540;
		var gbc = new GridBagConstraints();

		// TODO: use GridBagLayout
		JPanel routingPanel = new JPanel();
		routingPanel.setBounds(0, 0, windowWidth / 2, windowHeight);

		JLabel routingTitle = new JLabel("Static Routing Table");
		routingPanel.add(routingTitle);

		routingTable = new JTable(new DefaultTableModel(new String[][] {},
				new String[]{"Destination", "Netmask", "Gateway", "Flag", "Interface", "Metric"}));
		JScrollPane sp=new JScrollPane(routingTable);
		routingPanel.add(sp);

		JButton addRoutingTblBtn = new JButton("Add");
		addRoutingTblBtn.addActionListener(e -> onClickAddRoutingTableBtn());
		routingPanel.add(addRoutingTblBtn);

		JButton removeRoutingTblBtn = new JButton("Remove");
		removeRoutingTblBtn.addActionListener(e -> onClickDeleteRoutingTableBtn());
		routingPanel.add(removeRoutingTblBtn);

		JPanel cachePanel = new JPanel();
		cachePanel.setLayout(new GridBagLayout());
		cachePanel.setBounds(windowWidth / 2, 7 , windowWidth / 2 - 20 , windowHeight - 65);

		gbc.weightx = 1;

		JLabel cacheTitle = new JLabel("ARP Cache Table");
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0.2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		cachePanel.add(cacheTitle, gbc);

		arpCacheTable = new JTable(new DefaultTableModel(new String[][] {},
				new String[]{"IP Address", "Ethernet Address", "Flag", "Interface"}));
		JScrollPane sp2 =new JScrollPane(arpCacheTable);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 0.6;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		cachePanel.add(sp2, gbc);

		JButton deleteCacheTblBtn = new JButton("Delete");
		deleteCacheTblBtn.addActionListener(e -> onClickDeleteCacheTableBtn());
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0.2;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		cachePanel.add(deleteCacheTblBtn, gbc);


		JFrame f=new JFrame();//creating instance of JFrame
		f.add(routingPanel);
		f.add(cachePanel);
		f.setSize(windowWidth,windowHeight);
		f.setLocationRelativeTo(null);
		f.setLayout(null);//using no layout managers
		f.setVisible(true);//making the frame visible
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public ApplicationLayer(String pName)
	{
		pLayerName = pName;
		CreateWindow();
	}

	public static void main(String[] args) {
		
		layerManager.AddLayer(new NILayer(Constants.NILayerName));
		layerManager.AddLayer(new ARPLayer(Constants.ARPLayerName));
		layerManager.AddLayer(new EthernetLayer(Constants.EthLayerName));
		layerManager.AddLayer(new IPLayer(Constants.IPLayerName));
		layerManager.AddLayer(new ApplicationLayer(Constants.AppLayerName));
		layerManager.ConnectLayers();
		IPLayer ip = (IPLayer) layerManager.GetLayer(Constants.IPLayerName);
		((NILayer)layerManager.GetLayer(Constants.NILayerName)).Receive();
		
		/* Longest entry test
		ip.addEntry(new ROUTING_ENRTY("192.168.56.0","255.255.255.0","0.0.0.0","UH","0","1"));
		ip.addEntry(new ROUTING_ENRTY("192.168.0.0","255.255.0.0","0.0.0.0","UH","2","1"));
		ip.addEntry(new ROUTING_ENRTY("192.168.0.0","255.255.255.0","0.0.0.0","UH","1","1"));
		ip.getEntry(new byte[] {(byte) 192,(byte) 168,0,1});
		*/
	}
	public class RoutingTableAddPopup extends JFrame {

		private JPanel contentPane;
		
		private JTextField DstIp;
		private JTextField NetMask;
		private JTextField Gateway;

		
		private JCheckBox FlagU;
		private JCheckBox FlagG;
		private JCheckBox FlagH;

		private JComboBox<String> Ninterface;
		
		RoutingTableAddPopup(){

			setTitle("Routing table add");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 490, 295);
			contentPane = new JPanel();
			
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel ProxyEntryIpLabel = new JLabel("Destination");
			ProxyEntryIpLabel.setFont(new Font("돋움", Font.BOLD, 10));
			ProxyEntryIpLabel.setBounds(25, 35, 75, 35);
			contentPane.add(ProxyEntryIpLabel);
			
			JLabel proxyEntryEthernetLabel = new JLabel("NetMask");
			proxyEntryEthernetLabel.setFont(new Font("돋움", Font.BOLD, 10));
			proxyEntryEthernetLabel.setBounds(25, 70, 135, 35);
			contentPane.add(proxyEntryEthernetLabel);
			
			DstIp = new JTextField("192.168.1.0");
			DstIp.setBounds(170, 35, 220, 30);
			contentPane.add(DstIp);
			DstIp.setColumns(10);
			
			NetMask = new JTextField("255.255.255.0");
			NetMask.setColumns(10);
			NetMask.setBounds(170, 70, 220, 30);
			contentPane.add(NetMask);
			
			JButton routeTableAddBtn = new JButton("Add");
			routeTableAddBtn.setBounds(105, 215, 100, 25);
			contentPane.add(routeTableAddBtn);
			
			JButton routeTableCancelBtn = new JButton("Cancel");
			routeTableCancelBtn.setBounds(266, 215, 100, 25);
			contentPane.add(routeTableCancelBtn);
			routeTableCancelBtn.addActionListener(e->{
				dispose();
			});
			
			JLabel gatewayLabel = new JLabel("Gateway");
			gatewayLabel.setFont(new Font("돋움", Font.BOLD, 10));
			gatewayLabel.setBounds(25, 105, 135, 35);
			contentPane.add(gatewayLabel);
			
			Gateway = new JTextField("0.0.0.0");
			Gateway.setColumns(10);
			Gateway.setBounds(170, 105, 220, 30);
			contentPane.add(Gateway);
			
			JLabel flagLabel = new JLabel("Flag");
			flagLabel.setFont(new Font("돋움", Font.BOLD, 10));
			flagLabel.setBounds(25, 140, 135, 35);
			contentPane.add(flagLabel);
			
			FlagU = new JCheckBox("UP");
			FlagG = new JCheckBox("Gateway");
			FlagH = new JCheckBox("Host");
			FlagU.setBounds(170, 140, 50 , 30);
			FlagG.setBounds(220, 140, 100, 30);
			FlagH.setBounds(320, 140, 80 , 30);

			contentPane.add(FlagU);
			contentPane.add(FlagG);
			contentPane.add(FlagH);

			JLabel interfaceLabel = new JLabel("Interface");
			interfaceLabel.setFont(new Font("돋움", Font.BOLD, 10));
			interfaceLabel.setBounds(25, 175, 135, 35);
			contentPane.add(interfaceLabel);
			
			Ninterface = new JComboBox<String>();
			Ninterface.setBounds(170,180,220,20);
			contentPane.add(Ninterface);
			NILayer NI = (NILayer) layerManager.GetLayer(Constants.NILayerName); 
			for (PcapIf pcapIf : NI.getAdapterList()) {
				try {
					Ninterface.addItem(pcapIf.getDescription() + "[" + MacToStr(pcapIf.getHardwareAddress()) + "]");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			routeTableAddBtn.addActionListener(e -> {
				String dst = DstIp.getText().trim();
				String netMask = NetMask.getText().trim();
				String gateway = Gateway.getText().trim();
				String nInf = Integer.toString(Ninterface.getSelectedIndex());
				String flag = new String();
				if (!dst.equals("") && !netMask.equals("")&& !gateway.equals("") && !nInf.equals("")) {
					if (FlagU.isSelected()) flag+="U";
					if (FlagG.isSelected()) flag+="G";
					if (FlagH.isSelected()) flag+="H";
					IPLayer ip = (IPLayer) layerManager.GetLayer(Constants.IPLayerName);
					ip.addEntry(new ROUTING_ENRTY(dst,netMask,gateway,flag,nInf,"1"));
				}
			});
	
			setVisible(true);
		}

		
	}
	public void onClickAddRoutingTableBtn()
	{
		new RoutingTableAddPopup();
	}

	public void onClickDeleteRoutingTableBtn()
	{
		int row = routingTable.getSelectedRow();
		if (row == -1) {
			return;
		}
		IPLayer ip = (IPLayer) layerManager.GetLayer(Constants.IPLayerName);
		ip.removeEntry(StrToIp((String)routingTable.getModel().getValueAt(row, 0)));
		

	}

	public void onClickDeleteCacheTableBtn()
	{
		int row = arpCacheTable.getSelectedRow();
		if (row == -1) {
			return;
		}
		ARPLayer arp = (ARPLayer) layerManager.GetLayer(Constants.ARPLayerName);
		arp.cacheRemove(StrToIp((String)arpCacheTable.getModel().getValueAt(row, 0)));
	}
	
	public void updateRoutingTable(ArrayList<ROUTING_ENRTY> routing_table) {
		((DefaultTableModel)routingTable.getModel()).setRowCount(0);
		for (ROUTING_ENRTY entry : routing_table)
			((DefaultTableModel)routingTable.getModel()).addRow(new String[]{entry.dst, entry.mask, entry.gateway, entry.flag, entry.Interface});
	}
	
	public void updateARPCacheTable(ArrayList<ARP_CACHE> cache_table) {
		
		DefaultTableModel arpModel = (DefaultTableModel)arpCacheTable.getModel();
		
		arpModel.setRowCount(0);
		//cacheTable -> GUI
		Iterator<ARP_CACHE> iter = cache_table.iterator();
    	while(iter.hasNext()) {
    		ARP_CACHE cache = iter.next();
    		String[] row = new String[4];
    		
    		row[0] = IpToStr(cache.ip);
    		if(cache.status == false) {
    			row[1] = "??????????";
    			row[2] = "incomplete";
    		}else{
    			row[1] = MacToStr(cache.mac);
    			row[2] = "complete";
    			row[3] = Integer.toString(cache.port); 
    		    arpModel.addRow(row);
    		}
    	}
	}

}