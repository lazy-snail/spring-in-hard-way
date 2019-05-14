package examples.netbeans.UI;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JTextField;

public class Cell extends JTextField {
    public Object value;

    Cell(Object o) {
        value = o;
        this.setFont(new Font("Segoe UI Light", 0, 20));
        setText(o.toString());
        setEditable(false);
    }

    Cell(String s) {
        this.setFont(new Font("Segoe UI Light", Font.BOLD, 25));
        setText(s);
        setEditable(false);
    }

    final public void centerText() {
        Graphics2D g2d = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR).createGraphics();
        int w = g2d.getFontMetrics(getFont()).stringWidth(getText());
        w = getWidth() - w;
        w /= 2;
        w = Math.max(0, w);
        int sw = g2d.getFontMetrics(getFont()).stringWidth(" ");
        StringBuilder sb = new StringBuilder();
        while (w > 0) {
            sb.append(" ");
            w -= sw;
        }

        setText(sb.append(getText()).toString());
    }

}
