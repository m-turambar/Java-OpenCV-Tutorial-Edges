import javax.swing.JFrame;
import javax.swing.JSlider;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.atul.JavaOpenCV.Imshow;

public class Iterator implements Runnable {
	Mat src = new Mat();
	String type;
	JSlider[] barra;
	JFrame[] frame;
	int n;
	int posx;
	

	public Iterator(Mat src, String type, int posx) {
		this.type = type;
		this.src = src.clone();
		this.posx = posx;
	} // src.clone si quieres una matriz distinta

	public void run() {
		Imshow ventana = new Imshow("Iteration");
		ventana.Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.Window.setLocation(posx, 0);
		Mat edges = new Mat();

		switch (type) {
		case "Canny":
			n = 2;
			break;
		case "Laplacian":
			n = 3;
			break;
		case "HoughL":
			n=5;
			break;
		}
		barra = new JSlider[n];
		frame = new JFrame[n];

		for (int h = 0; h < n; h++) {

			barra[h] = new JSlider(JSlider.HORIZONTAL, 0, 400, 1);
			frame[h] = new JFrame("Panel iterador");
			frame[h].setVisible(true);
			frame[h].setSize(src.width(), 90);
			frame[h].setLocation(posx, src.height() + 30 + 90 * h);
			frame[h].add(barra[h]);
			barra[h].setPaintTicks(true);
			barra[h].setMajorTickSpacing(20);
		}
		
		if(type == "Laplacian"){barra[0].setMaximum(15);barra[1].setMaximum(50);}
		
		int count = 0;
		while (true) {
			switch (type) {
			case "Canny":
				Imgproc.Canny(src, edges, barra[0].getValue(),
						barra[1].getValue());
				break;
			case "Laplacian":
				Imgproc.Laplacian(src, edges, 0, barra[0].getValue() * 2 + 1,
						barra[1].getValue(), barra[2].getValue());
				break;
				
			case "HoughL":
				Mat lineas = new Mat();
				Mat color = Mat.zeros(src.size(),CvType.CV_8UC3);
				Mat bordes = new Mat(src.size(),CvType.CV_8UC1);
				Imgproc.Canny(src, bordes, barra[0].getValue(), barra[1].getValue());
				Imgproc.HoughLinesP(bordes, lineas, 1, Math.PI/180, barra[2].getValue(), barra[3].getValue(), barra[4].getValue());
				lineas = Funciones.filtrarLineas(lineas, 20);
				if(!lineas.empty())Funciones.dibujarLineas(color, lineas);
				edges = color.clone();
			}
			
			for (int i = 0; i < src.cols() - 1; i += 2) {
				Mat col = src.colRange(i + 1, i + 2);
				Mat edgecol = edges.colRange(i + 1, i + 2);
				col.copyTo(edgecol);
			}
			ventana.showImage(edges);
			ventana.Window.setTitle("Iteration: " + count);
			count++;
			Funciones.titulosBarras(frame,barra,n);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}