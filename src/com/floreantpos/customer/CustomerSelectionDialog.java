/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * ************************************************************************
 */
package com.floreantpos.customer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;

import com.floreantpos.Messages;
import com.floreantpos.bo.ui.BackOfficeWindow;
import com.floreantpos.main.Application;
import com.floreantpos.model.Customer;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.CustomerDAO;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.swing.POSTextField;
import com.floreantpos.swing.PosSmallButton;
import com.floreantpos.ui.dialog.BeanEditorDialog;
import com.floreantpos.ui.dialog.POSDialog;
import com.floreantpos.ui.dialog.POSMessageDialog;
import com.floreantpos.ui.forms.CustomerForm;

public class CustomerSelectionDialog extends POSDialog {

	private PosSmallButton btnCreateNewCustomer;
	private CustomerTable customerTable;
	private POSTextField tfMobile;
	private POSTextField tfLoyaltyNo;
	private POSTextField tfName;
	private PosSmallButton btnInfo;
	protected Customer selectedCustomer;
	private PosSmallButton btnRemoveCustomer;

	private Ticket ticket;

	public CustomerSelectionDialog() {
		super(BackOfficeWindow.getInstance(),true);
		setTitle(Messages.getString("CustomerSelectionDialog.3")); //$NON-NLS-1$
	}

	public CustomerSelectionDialog(Ticket ticket) {
		this.ticket = ticket;

		setTitle(Messages.getString("CustomerSelectionDialog.0")); //$NON-NLS-1$

		loadCustomerFromTicket();
	}

	@Override
	public void initUI() {
		setPreferredSize(new Dimension(690, 553));
		getContentPane().setLayout(new MigLayout("", "[549px,grow]", "[grow][][shrink 0,fill][grow][grow]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(panel_4, "cell 0 0,grow"); //$NON-NLS-1$
		panel_4.setLayout(new MigLayout("", "[grow][][][]", "[grow][][][]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JLabel lblNewLabel = new JLabel(""); //$NON-NLS-1$
		panel_4.add(lblNewLabel, "cell 0 0 1 3,grow"); //$NON-NLS-1$

		JLabel lblByPhone = new JLabel(Messages.getString("CustomerSelectionDialog.1")); //$NON-NLS-1$
		panel_4.add(lblByPhone, "cell 1 0"); //$NON-NLS-1$

		tfMobile = new POSTextField();
		panel_4.add(tfMobile, "cell 2 0"); //$NON-NLS-1$
		tfMobile.setColumns(16);

		PosSmallButton psmlbtnSearch = new PosSmallButton();
		panel_4.add(psmlbtnSearch, "cell 3 0 1 3,growy"); //$NON-NLS-1$
		psmlbtnSearch.setFocusable(false);
		psmlbtnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSearchCustomer();
			}
		});
		psmlbtnSearch.setText(Messages.getString("CustomerSelectionDialog.15")); //$NON-NLS-1$

		JLabel lblByName = new JLabel(Messages.getString("CustomerSelectionDialog.16")); //$NON-NLS-1$
		panel_4.add(lblByName, "cell 1 1,alignx trailing"); //$NON-NLS-1$

		tfLoyaltyNo = new POSTextField();
		panel_4.add(tfLoyaltyNo, "cell 2 1"); //$NON-NLS-1$
		tfLoyaltyNo.setColumns(16);

		JLabel lblByEmail = new JLabel(Messages.getString("CustomerSelectionDialog.19")); //$NON-NLS-1$
		panel_4.add(lblByEmail, "cell 1 2,alignx trailing"); //$NON-NLS-1$

		tfName = new POSTextField();
		panel_4.add(tfName, "cell 2 2"); //$NON-NLS-1$
		tfName.setColumns(16);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(10, 0, 0, 0));
		panel_4.add(panel_2, "cell 0 3 4 1,growx"); //$NON-NLS-1$
		panel_2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setFocusable(false);
		panel_2.add(scrollPane, BorderLayout.CENTER);

		customerTable = new CustomerTable();
		customerTable.setModel(new CustomerListTableModel());
		customerTable.setFocusable(false);
		customerTable.setRowHeight(35);
		customerTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		customerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedCustomer = customerTable.getSelectedCustomer();
				if(selectedCustomer != null) {
					//					btnInfo.setEnabled(true);
				}
				else {
					btnInfo.setEnabled(false);
				}
			}
		});
		scrollPane.setViewportView(customerTable);

		JPanel panel = new JPanel();
		panel_2.add(panel, BorderLayout.SOUTH);

		btnInfo = new PosSmallButton();
		btnInfo.setFocusable(false);
		panel.add(btnInfo);
		btnInfo.setEnabled(false);
		btnInfo.setText(Messages.getString("CustomerSelectionDialog.23")); //$NON-NLS-1$

		PosSmallButton btnHistory = new PosSmallButton();
		btnHistory.setEnabled(false);
		btnHistory.setText(Messages.getString("CustomerSelectionDialog.24")); //$NON-NLS-1$
		panel.add(btnHistory);

		btnCreateNewCustomer = new PosSmallButton();
		btnCreateNewCustomer.setFocusable(false);
		panel.add(btnCreateNewCustomer);
		btnCreateNewCustomer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doCreateNewCustomer();
			}
		});
		btnCreateNewCustomer.setText(Messages.getString("CustomerSelectionDialog.25")); //$NON-NLS-1$

		btnRemoveCustomer = new PosSmallButton();
		btnRemoveCustomer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doRemoveCustomerFromTicket();
			}
		});
		btnRemoveCustomer.setText(Messages.getString("CustomerSelectionDialog.26")); //$NON-NLS-1$
		panel.add(btnRemoveCustomer);

		PosSmallButton btnSelect = new PosSmallButton();
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Customer customer = customerTable.getSelectedCustomer();

				selectedCustomer = customer;

				if(customer == null) {
					POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("CustomerSelectionDialog.27")); //$NON-NLS-1$
					return;
				}

				doSetCustomer(customer);
				setCanceled(false);
				dispose();
			}
		});
		btnSelect.setText(Messages.getString("CustomerSelectionDialog.28")); //$NON-NLS-1$
		panel.add(btnSelect);

		PosSmallButton btnCancel = new PosSmallButton();
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setCanceled(true);
				dispose();
			}
		});
		btnCancel.setText(Messages.getString("CustomerSelectionDialog.29")); //$NON-NLS-1$
		panel.add(btnCancel);

		JPanel panel_3 = new JPanel(new BorderLayout());
		getContentPane().add(panel_3, "cell 0 1,grow, gapright 2px"); //$NON-NLS-1$

		com.floreantpos.swing.QwertyKeyPad qwertyKeyPad = new com.floreantpos.swing.QwertyKeyPad();
		panel_3.add(qwertyKeyPad);
		tfName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSearchCustomer();
			}
		});
		tfLoyaltyNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSearchCustomer();
			}
		});
		tfMobile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSearchCustomer();
			}
		});
	}

	private void loadCustomerFromTicket() {
		String customerIdString = ticket.getProperty(Ticket.CUSTOMER_ID);
		if(StringUtils.isNotEmpty(customerIdString)) {
			int customerId = Integer.parseInt(customerIdString);
			Customer customer = CustomerDAO.getInstance().get(customerId);

			List<Customer> list = new ArrayList<Customer>();
			list.add(customer);
			customerTable.setModel(new CustomerListTableModel(list));
		}
	}

	protected void doSetCustomer(Customer customer) {
		if(ticket != null) {
			ticket.setCustomer(customer);
			TicketDAO.getInstance().saveOrUpdate(ticket);
		}

	}

	protected void doRemoveCustomerFromTicket() {
		int option = POSMessageDialog.showYesNoQuestionDialog(this,
				Messages.getString("CustomerSelectionDialog.2"), Messages.getString("CustomerSelectionDialog.32")); //$NON-NLS-1$ //$NON-NLS-2$
		if(option != JOptionPane.YES_OPTION) {
			return;
		}

		ticket.removeCustomer();
		TicketDAO.getInstance().saveOrUpdate(ticket);
		setCanceled(false);
		dispose();
	}

	protected void doSearchCustomer() {
		String mobile = tfMobile.getText();
		String name = tfName.getText();
		String loyalty = tfLoyaltyNo.getText();

		if(StringUtils.isEmpty(mobile) && StringUtils.isEmpty(loyalty) && StringUtils.isEmpty(name)) {
			List<Customer> list = CustomerDAO.getInstance().findAll();
			customerTable.setModel(new CustomerListTableModel(list));
			return;
		}

		List<Customer> list = CustomerDAO.getInstance().findBy(mobile, loyalty, name);
		customerTable.setModel(new CustomerListTableModel(list));
	}

	protected void doCreateNewCustomer() {
		boolean setKeyPad = true;
		CustomerForm form = new CustomerForm(setKeyPad);
		form.enableCustomerFields(true);
		BeanEditorDialog dialog = new BeanEditorDialog(form);
		dialog.open();

		if(!dialog.isCanceled()) {
			selectedCustomer = (Customer) form.getBean();

			CustomerListTableModel model = (CustomerListTableModel) customerTable.getModel();
			model.addItem(selectedCustomer);
		}
	}

	@Override
	public String getName() {
		return "C"; //$NON-NLS-1$
	}

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setRemoveButtonEnable(boolean enable) {
		btnRemoveCustomer.setEnabled(enable);
	}
}
