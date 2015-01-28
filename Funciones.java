import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JSlider;

import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.core.Algorithm;
import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoBM;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.contrib.Contrib;
import org.opencv.contrib.FaceRecognizer;
import org.opencv.contrib.StereoVar;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.gpu.DeviceInfo;
import org.opencv.gpu.Gpu;
import org.opencv.gpu.TargetArchs;
import org.opencv.ml.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.objdetect.Objdetect;
import org.opencv.photo.Photo;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.GenericDescriptorMatcher;
import org.opencv.features2d.KeyPoint;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.utils.Converters;
import org.opencv.core.MatOfPoint;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.KalmanFilter;
import org.opencv.imgproc.Imgproc;
import com.atul.JavaOpenCV.Imshow;

public class Funciones {

	
	static CascadeClassifier cascadon = new CascadeClassifier( 
			"C:\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface.xml");
	
	
	
	public static void funciones() {
		DeviceInfo dev = new DeviceInfo();
		Video vid = new Video();
		Converters conv = new Converters();
		System.out.println(dev.name());

	}

	public static Mat foto(boolean gr, boolean bw, int thr) {
		VideoCapture cap = new VideoCapture(0);
		if (!cap.isOpened()) {
			System.out.println("no pudo conectar");
		} else
			System.out.println("Camara encontrada: " + cap.toString());
		Mat imagen = new Mat();
		cap.retrieve(imagen);
		if (gr)
			Imgproc.cvtColor(imagen, imagen, Imgproc.COLOR_BGR2GRAY);
		if (bw)
			Imgproc.threshold(imagen, imagen, thr, 255, Imgproc.THRESH_BINARY);
		cap.release();
		return imagen;
	}

	public static Mat cargar(String nimagen, boolean gr, boolean bw, int thr) {
		Mat imagen;
		imagen = Highgui.imread("C:\\OpenCV4Android\\" + nimagen);
		if (gr)
			Imgproc.cvtColor(imagen, imagen, Imgproc.COLOR_BGR2GRAY);
		if (bw)
			Imgproc.threshold(imagen, imagen, thr, 255, Imgproc.THRESH_BINARY);
		return imagen;
	}

	public static void mostrar(Mat src, int x, int y) {
		Imshow ventana = new Imshow("" + src.channels() + ", ");
		ventana.Window.setResizable(true);
		ventana.Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.showImage(src);
		ventana.Window.setLocation(x, y);
	}
	
	public static void guardar(Mat src, String nom){
		Highgui.imwrite("C:\\OpenCV4Android\\" + nom, src);
	}
	
	public static void video (){
		Mat m = new Mat();
		VideoCapture vcam = new VideoCapture(1);
		Imshow window = new Imshow("tu mama");
	while (!vcam.isOpened());
	while (m.empty()) {vcam.retrieve(m);}
	while (true) {
		vcam.retrieve(m);
		window.showImage(m);
		if (!window.Window.isShowing())break;
	}
	vcam.release();
}
	
	public static void videoCaras (){
		Mat m = new Mat();
		MatOfRect caras = new MatOfRect();
		VideoCapture vcam = new VideoCapture(1);
		Imshow window = new Imshow("tu cara");
		window.Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	while (!vcam.isOpened());
	while (m.empty()) {vcam.retrieve(m);}
	while (true) {
		vcam.retrieve(m);
		cascadon.detectMultiScale(m, caras); 
		for (Rect rect : caras.toArray()){
			Core.rectangle(m, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0,0,255));
		}
		window.showImage(m);
	}
	
}
	
	public static Mat esqueletizar(Mat src, Mat kernel){
		Mat skeleton = Mat.zeros(src.size(), CvType.CV_8UC1);
		Mat temporal = new Mat(src.size(),CvType.CV_8UC1);
		boolean completo=false;
		double max;
		while(!completo){
			Imgproc.morphologyEx(src, temporal, Imgproc.MORPH_OPEN, kernel);
//			Core.bitwise_not(temporal, temporal);
//			Core.bitwise_and(src, temporal, temporal);
			Core.subtract(src, temporal, temporal);
			Core.bitwise_or(skeleton, temporal, skeleton);
			Imgproc.erode(src, src, kernel);
			max = Core.countNonZero(src);
			if(max==0)completo=true;
		}
		return skeleton;
	}
	
	public static Mat kernelEsq(int dim,int hueco,int t0123){
		Scalar cero = new Scalar(0);
		Mat k = Imgproc.getStructuringElement( 1,
                new Size( dim, dim ),
                new Point( (dim-1)/2, (dim-1)/2)) ;
		if(t0123==0){
			Mat r = k.colRange((dim-1)/2-hueco,dim).rowRange((dim-1)/2-hueco,dim);
		r.setTo(cero);}
		if(t0123==1){
			Mat r = k.colRange((dim-1)/2-hueco,dim).rowRange(0,(dim-1)/2+hueco+1);
		r.setTo(cero);}
		if(t0123==2){
			Mat r = k.colRange(0,(dim-1)/2+hueco+1).rowRange((dim-1)/2-hueco,dim);
		r.setTo(cero);}
		if(t0123==3){
			Mat r = k.colRange(0,(dim-1)/2+hueco+1).rowRange(0,(dim-1)/2+hueco+1);
		r.setTo(cero);}
		
		return k;
	}
	
	
	public static Mat atacarEsquinas(Mat src,int dim,int hueco){
		Mat temp = Mat.zeros(src.size(), CvType.CV_8UC1);
		Imgproc.erode(src, temp, kernelEsq(dim,hueco,0));
		Core.bitwise_or(src, temp, src);
		Imgproc.erode(src, temp, kernelEsq(dim,hueco,1));
		Core.bitwise_or(src, temp, src);
		Imgproc.erode(src, temp, kernelEsq(dim,hueco,2));
		Core.bitwise_or(src, temp, src);
		Imgproc.erode(src, temp, kernelEsq(dim,hueco,3));
		Core.bitwise_or(src, temp, src);
		return src;
	}
	
	public static void titulosBarras(JFrame[] frames, JSlider[] sliders, int n){
		for(int i=0; i<n;i++){
			frames[i].setTitle(""+sliders[i].getValue());
		}
	}
	
	public static void dibujarLineas(Mat src, Mat lines){
		for (int x = 0; x < lines.cols(); x++) {
	          double[] vec = lines.get(0, x);
	          double x1 = vec[0], 
	                 y1 = vec[1],
	                 x2 = vec[2],
	                 y2 = vec[3];
	          Point start = new Point(x1, y1);
	          Point end = new Point(x2, y2);
	          Core.circle(src, start, 10, new Scalar(200,200,0));
	          Core.circle(src, end, 10, new Scalar(0,200,200));
	          Core.line(src, start, end, new Scalar(0,0,255),1);}
	          }
	
	public static Mat filtrarLineas(Mat lines, int cercania){
		if(!lines.empty()){
		double x1temp, y1temp,x2temp, y2temp;x1temp= y1temp=x2temp= y2temp = 10000;
		Mat lineasfiltradas=new Mat(1,lines.cols(),CvType.CV_32SC4); int ind=0;
		Mat copia = lines.clone();
		double[] ord = new double[lines.cols()];
		double[] ordorig = new double[lines.cols()];
		//primero ordena la lista de menor a mayor
		for (int x = 0; x < lines.cols(); x++){
			 double[] vec = lines.get(0, x);
	          ord[x] = vec[0];        //saca los valores de xi  
		}
		ordorig = ord.clone();
		Arrays.sort(ord);
		for (int x = 0; x < ord.length; x++){
			for (int j = 0; j < ord.length; j++){
				if(ord[x] == ordorig[j])lines.put(0, x, copia.get(0, j));	
			}
		}
		for (int x = 0; x < lines.cols(); x++) {
	          double[] vec = lines.get(0, x);
	          double x1 = vec[0], 
	                 y1 = vec[1],
	                 x2 = vec[2],
	                 y2 = vec[3];
	          if(!(Math.abs(x1-x1temp)<cercania&&Math.abs(x2-x2temp)<cercania&&Math.abs(y1-y1temp)<cercania&&Math.abs(y2-y2temp)<cercania)){
	        	  lineasfiltradas.put(0, ind, vec); ind++;
	        	  x1temp = x1;x2temp = x2;y1temp = y1;y2temp = y2;
	         }
	          }
		lineasfiltradas = lineasfiltradas.colRange(0, ind);
		return lineasfiltradas;
		}return null;}
	
	
}