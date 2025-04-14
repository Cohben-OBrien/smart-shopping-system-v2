package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Shopping System v1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600); // Adjust height as needed
            frame.setLocationRelativeTo(null);

            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new BorderLayout());

            // 1. Title Label (at the top)
            JLabel titleLabel = new JLabel("Smart Shopping System v1");
            titleLabel.setFont(new Font("Roboto", Font.BOLD, 36));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            contentPane.add(titleLabel, BorderLayout.NORTH);

            // 2. Panel for Buttons (in the center)
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JButton productsButton = new JButton("Add Products");
            productsButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "View Products functionality."));
            buttonPanel.add(productsButton);

            JButton cartButton = new JButton("Record Sale");
            cartButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "View Cart functionality."));
            buttonPanel.add(cartButton);

            JButton ordersButton = new JButton("Show Sales Report");
            ordersButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "View Orders functionality."));
            buttonPanel.add(ordersButton);

            JButton settingsButton = new JButton("Show Low Stock Report");
            settingsButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Settings functionality."));
            buttonPanel.add(settingsButton);

            contentPane.add(buttonPanel, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}