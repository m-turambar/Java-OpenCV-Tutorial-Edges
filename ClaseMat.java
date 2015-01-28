import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
//


public class ClaseMat {

	
	public static void main(String[] args) {
		System.loadLibrary("opencv_java248");
		Mat src = Funciones.cargar("nocontraste.png", false, false, 0);
		Funciones.mostrar(src, src.width()*2, 0);
		Mat src1 = Funciones.cargar("cuadrado.jpg", false, false, 0);
		//Mat src = Funciones.foto(false, false, 0);
		//Funciones.guardar(src, "yo.jpg");
		Mat colores[] = new Mat[3];
		List<Mat> lista = new ArrayList<Mat>();
		for(Mat x:colores) lista.add(x);
		Core.split(src, lista);
		Funciones.mostrar(lista.get(0), 0, src.height());
		Funciones.mostrar(lista.get(1), 0, src.height()*2);
		Funciones.mostrar(lista.get(2), 0, src.height()*3);
		
		Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(src1, src1, Imgproc.COLOR_BGR2GRAY);
	
		
//		Thread t1 = new Thread(new Iterator(src,"Canny",0));
//		Thread t2 = new Thread(new Iterator(src,"Laplacian",src.width()));
//		Thread t3 = new Thread(new Iterator(src1,"HoughL",0));
//		t1.start();
//		t2.start();
//		t3.start();
	Mat dst = new Mat();	
		Imgproc.equalizeHist(src, dst);
		Funciones.mostrar(src, 0, 0);
		Funciones.mostrar(dst, src.width(), 0);
		for(int i=0;i<lista.size();i++){
			Imgproc.equalizeHist(lista.get(i), lista.get(i));
		}
		
		Funciones.mostrar(lista.get(0), src.width(), src.height());
		Funciones.mostrar(lista.get(1), src.width(), src.height()*2);
		Funciones.mostrar(lista.get(2), src.width(), src.height()*3);
		Core.merge(lista, src);
		Funciones.mostrar(src, src.width()*3, 0);
	}

}
