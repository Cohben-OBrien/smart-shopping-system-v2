package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Shopping System v1");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500); // Adjust height as needed
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

            // 3. Image Loading (under the buttons - BorderLayout.SOUTH)
            JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            File imageFile = new File("parrot.jpg"); // Ensure "parrot.jpg" is in the correct location
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon("parrot.jpg");
                Image originalImage = originalIcon.getImage();
                int desiredWidth = 200; // Adjust as needed
                int desiredHeight = 150; // Adjust as needed
                Image scaledImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                JLabel imageLabel = new JLabel(scaledIcon);
                imagePanel.add(imageLabel);
                contentPane.add(imagePanel, BorderLayout.SOUTH);
            } else {
                JLabel errorLabel = new JLabel("Error: parrot.jpg not found in: " + new File(".").getAbsolutePath());
                imagePanel.add(errorLabel);
                contentPane.add(imagePanel, BorderLayout.SOUTH);
            }

            frame.setVisible(true);
        });
    }
}