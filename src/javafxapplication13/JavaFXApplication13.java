/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication13;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import javafx.geometry.Insets;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacv.OpenCVFrameConverter;
/**
 *
 * @author Lenovo
 */
public class JavaFXApplication13 extends Application {
    String filePath;
    ImageView image ;
    ScrollPane spane;
    ArrayList <BufferedImage> list_BI = new ArrayList<>();
    ArrayList<Frame> my_frames = new ArrayList<>();
    ArrayList <javafx.scene.image.Image> list_I = new ArrayList<>();
    ArrayList <CheckBox> check = new ArrayList<>();
    ArrayList <StackPane> stack= new ArrayList<>();
    ArrayList <ImageView> AR_IM = new ArrayList<>();
    ArrayList <ArrayList<Image>> undo_list=new ArrayList<>();
    int videoHeight;
    int videoWidth;
    FFmpegFrameGrabber frameGrabber ;
    public double frameRate;
    StackPane navBar;
    TilePane root_h;
    VBox root;
    @Override  
    public void start(Stage primaryStage) {
        Button EditVideo = new Button();
        EditVideo.setText("                         Edit Video                                ");
        EditVideo.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
        public void handle(ActionEvent event) {
        Stage stage1 = new Stage();
        navBar = new StackPane();
        image = new ImageView();
        spane = new ScrollPane();
        root_h = new TilePane();
         Button change_order = new Button();
         change_order.setText("change order of the frames");
        Button delete = new Button();
        delete.setText("DELETE");
         Button undo = new Button();
         Button save = new Button();
         save.setText("SAVE VIDEO");
         undo.setText("UNDO");
         Button watermark = new Button();
         watermark.setText("WATER MARK");
        navBar.getChildren().addAll(delete,change_order,undo,save,watermark);
        delete.setTranslateX(-300);
         undo.setTranslateX(150);
         save.setTranslateX(300);
        change_order.setTranslateX(-150);
        watermark.setTranslateX(450);
    undo.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        System.out.println(undo_list.get(undo_list.size()-1).size());
        if (undo_list.size()==0){
        System.out.println("there is no edits yet");  
        }
        else{
        list_I = new ArrayList<>();
        for (int i=0 ; i< undo_list.get(undo_list.size()-1).size();i++){
            list_I.add(undo_list.get(undo_list.size()-1).get(i));
        }
        AR_IM = new ArrayList<>();
        check = new ArrayList<>();
        stack = new ArrayList<>();
        root_h = new TilePane();
        for (int i=0 ; i<list_I.size();i++){
          ImageView iv = new ImageView();
                 iv.setImage(list_I.get(i));
                 iv.setFitHeight(100);
                 iv.setFitWidth(100);
                 AR_IM.add(iv);
                 CheckBox c= new CheckBox();
                 c.setText(Integer.toString(i));
                 c.setTranslateY(60);
                 check.add(c);
                 StackPane s = new StackPane();
                 s.getChildren().addAll(AR_IM.get(i),check.get(i));
                 stack.add(s);   
        }
         for (int i=0 ; i<stack.size();i++){
         root_h.getChildren().add(stack.get(i));
         root_h.setVgap(20);
         root_h.setHgap(20);
         }
        spane.setContent(root_h);
        root_h.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getWidth(), spane.viewportBoundsProperty()));
        root_h.minHeightProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getHeight(), spane.viewportBoundsProperty())); 
        System.out.println("done");
        }
    }});
    save.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
         System.out.println("if you want to save video with its frame rate enter 0 or if you want to changr frame rate enter 1");
        int x = new Scanner(System.in).nextInt();
        while(x!=0 && x!=1){
            System.out.println("please enter 1 or 0");
             x = new Scanner(System.in).nextInt();
        }
        if (x==0){
        save(list_I,frameRate);
        }
        else{
            System.out.println("enter new frame rate");
            double y = new Scanner(System.in).nextDouble();
            save(list_I,y);
        }
        System.out.println("done");
        }});
    watermark.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
         undo_list.add(new ArrayList<Image>());
        for (int i=0 ; i<list_I.size();i++){
            undo_list.get(undo_list.size()-1).add(list_I.get(i));
        }
        System.out.println("enter i for image watermark or t for text watermark...");
        BufferedImage img;
        int height,width;
         Scanner s1=new Scanner(System.in); 
        char w=s1.next().charAt(0);
        if(w=='i')
        {
             System.out.println("enter  alpha value from 0 to 1");
             float aa=s1.nextFloat();
            try {
                Graphics2D g2d;
                AlphaComposite alphaChannel;
                FileChooser ff = new FileChooser();
                 File fff = ff.showOpenDialog(null);
                File watermaek = new File(fff.getPath());
                BufferedImage overlay= resize(ImageIO.read(watermaek),150,150);
                BufferedImage watermarkImage;
                for(int i=0;i<list_BI.size();i++)
                {
                    watermarkImage=new BufferedImage(list_BI.get(i).getWidth(),list_BI.get(i).getHeight(),BufferedImage.TYPE_INT_RGB); 
                    g2d=(Graphics2D)watermarkImage.getGraphics();
                    g2d.drawImage(list_BI.get(i), 0, 0,null);
                    alphaChannel=AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)aa);
                    g2d.setComposite(alphaChannel);
                    int topLeftX=(list_BI.get(i).getWidth())/5;
                    int topLeftY=(list_BI.get(i).getHeight())/5;
                    g2d.drawImage(overlay,topLeftX , topLeftY,null);
                    list_I.set(i,SwingFXUtils.toFXImage(watermarkImage, null));
                    g2d.dispose();
                }
            } catch (IOException ex) {
                Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
            }

                        AR_IM = new ArrayList<>();
        check = new ArrayList<>();
        stack = new ArrayList<>();
        root_h = new TilePane();
        for (int i=0 ; i<list_I.size();i++){
          ImageView iv = new ImageView();
                 iv.setImage(list_I.get(i));
                 iv.setFitHeight(100);
                 iv.setFitWidth(100);
                 AR_IM.add(iv);
                 CheckBox c= new CheckBox();
                 c.setText(Integer.toString(i));
                 c.setTranslateY(60);
                 check.add(c);
                 StackPane h = new StackPane();
                 h.getChildren().addAll(AR_IM.get(i),check.get(i));
                 stack.add(h);   
        }
         for (int i=0 ; i<stack.size();i++){
         root_h.getChildren().add(stack.get(i));
         root_h.setVgap(20);
         root_h.setHgap(20);
         }
         spane.setContent(root_h);
          root_h.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getWidth(), spane.viewportBoundsProperty()));
         root_h.minHeightProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getHeight(), spane.viewportBoundsProperty()));
        }
        if(w=='t')
        {
             System.out.println("enter  alpha value....");
             int aa=s1.nextInt();
               System.out.println("enter text");
                String text = new Scanner(System.in).next();
            for(int i=0;i<list_BI.size();i++)
            {
                height=list_BI.get(i).getHeight();
                width=list_BI.get(i).getWidth();
                img=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
                Graphics g=img.getGraphics();
                g.drawImage(list_BI.get(i),0 , 0, null);
                Font font=new Font("Arial",Font.PLAIN,200);
                g.setFont(font);
                g.setColor(new Color(1,0,0,(int)aa));
                g.drawString(text, width/2, height/2);
                g.dispose();
                list_I.set(i,SwingFXUtils.toFXImage(img, null));
            }
        

        AR_IM = new ArrayList<>();
        check = new ArrayList<>();
        stack = new ArrayList<>();
        root_h = new TilePane();
        for (int i=0 ; i<list_I.size();i++){
          ImageView iv = new ImageView();
                 iv.setImage(list_I.get(i));
                 iv.setFitHeight(100);
                 iv.setFitWidth(100);
                 AR_IM.add(iv);
                 CheckBox c= new CheckBox();
                 c.setText(Integer.toString(i));
                 c.setTranslateY(60);
                 check.add(c);
                 StackPane h = new StackPane();
                 h.getChildren().addAll(AR_IM.get(i),check.get(i));
                 stack.add(h);   
        }
         for (int i=0 ; i<stack.size();i++){
         root_h.getChildren().add(stack.get(i));
         root_h.setVgap(20);
         root_h.setHgap(20);
         }
         spane.setContent(root_h);
          root_h.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getWidth(), spane.viewportBoundsProperty()));
         root_h.minHeightProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getHeight(), spane.viewportBoundsProperty()));
            }  
        System.out.println("done");
    }
    });
    
    delete.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        undo_list.add(new ArrayList<Image>());
        for (int i=0 ; i<list_I.size();i++){
            undo_list.get(undo_list.size()-1).add(list_I.get(i));
        }
        for (int i=check.size()-1 ; i>=0;i--){
            if (check.get(i).isSelected()==true){
              list_I.remove(i);
              if (list_BI.get(i)!=null)
               list_BI.remove(i);
            }
        }
        AR_IM = new ArrayList<>();
        check = new ArrayList<>();
        stack = new ArrayList<>();
        root_h = new TilePane();
        for (int i=0 ; i<list_I.size();i++){
          ImageView iv = new ImageView();
                 iv.setImage(list_I.get(i));
                 iv.setFitHeight(100);
                 iv.setFitWidth(100);
                 AR_IM.add(iv);
                 CheckBox c= new CheckBox();
                 c.setText(Integer.toString(i));
                 c.setTranslateY(60);
                 check.add(c);
                 StackPane s = new StackPane();
                 s.getChildren().addAll(AR_IM.get(i),check.get(i));
                 stack.add(s);   
        }
         for (int i=0 ; i<stack.size();i++){
         root_h.getChildren().add(stack.get(i));
         root_h.setVgap(20);
         root_h.setHgap(20);
         }
        spane.setContent(root_h);
        root_h.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getWidth(), spane.viewportBoundsProperty()));
        root_h.minHeightProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getHeight(), spane.viewportBoundsProperty())); 
       
        } 
    });
        ////////////////////////
        change_order.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 undo_list.add(new ArrayList<Image>());
        for (int i=0 ; i<list_I.size();i++){
            undo_list.get(undo_list.size()-1).add(list_I.get(i));
        }
          Scanner s=new Scanner(System.in);  
          ArrayList <Image> temp1 = new ArrayList<>();
          ArrayList <Image> temp2 = new ArrayList<>();
          int n,first,to;  
        System.out.println("Enter the n"); 
        n=s.nextInt();
        System.out.println(n);
        System.out.println("Enter the first"); 
         first=s.nextInt();
          System.out.println("Enter the to"); 
           to=s.nextInt();
        if (first<to){
        if (to!=list_I.size()){
            for (int i=list_I.size()-1;i>to;i--){
          temp1.add(list_I.get(i));
          list_I.remove(i);
      }
      Collections.reverse(temp1);
       for (int i=first+n-1;i>=first;i--){
           temp2.add(list_I.get(i));
          list_I.remove(i); 
      }
     Collections.reverse(temp2);
      for (int i=0 ; i< temp2.size();i++){
          list_I.add(temp2.get(i));
      }
      for (int i=0 ; i< temp1.size();i++){
          list_I.add(temp1.get(i));
      }
        }
       else{
        for (int i=first+n-1;i>=first;i--){
           temp2.add(list_I.get(i));
          list_I.remove(i); 
      }
     Collections.reverse(temp2);  
     for (int i=0 ; i< temp2.size();i++){
          list_I.add(temp2.get(i));
      }
      }
        }
        else if (first>to){
           for (int i=first+n-1;i>=first;i--){
          temp1.add(list_I.get(i));
          list_I.remove(i);
      }
      Collections.reverse(temp1);
      for  (int i=list_I.size()-1;i>=to;i--){
          temp2.add(list_I.get(i));
          list_I.remove(i);
      }
      Collections.reverse(temp2);
      for(int i=0;i<temp1.size();i++){
          list_I.add(temp1.get(i));
      }
      for(int i=0;i<temp2.size();i++){
          list_I.add(temp2.get(i));
      } 
        }
          AR_IM = new ArrayList<>();
        check = new ArrayList<>();
        stack = new ArrayList<>();
        root_h = new TilePane();
        for (int i=0 ; i<list_I.size();i++){
          ImageView iv = new ImageView();
                 iv.setImage(list_I.get(i));
                 iv.setFitHeight(100);
                 iv.setFitWidth(100);
                 AR_IM.add(iv);
                 CheckBox c= new CheckBox();
                 c.setText(Integer.toString(i));
                 c.setTranslateY(60);
                 check.add(c);
                 StackPane h = new StackPane();
                 h.getChildren().addAll(AR_IM.get(i),check.get(i));
                 stack.add(h);   
        }
         for (int i=0 ; i<stack.size();i++){
         root_h.getChildren().add(stack.get(i));
         root_h.setVgap(20);
         root_h.setHgap(20);
         }
         spane.setContent(root_h);
          root_h.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getWidth(), spane.viewportBoundsProperty()));
         root_h.minHeightProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getHeight(), spane.viewportBoundsProperty()));

        System.out.println("done");
            }   
         });
        /////////////////
        root_h.setTranslateX(10);
        
        FileChooser fileChooser = new FileChooser();
    File file = fileChooser.showOpenDialog(null);   
    filePath=file.getPath();
    frameGrabber = new FFmpegFrameGrabber(file);
    try {
            frameGrabber.start();
        } catch (Exception ex) {
            Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
        }
    Frame frame;  
    int frameNo=frameGrabber.getLengthInFrames();  
    frameRate=frameGrabber.getFrameRate();
    System.out.println(frameNo); 
    System.out.println("Video has "+frameNo+" frames and has frame rate of "+frameGrabber.getFrameRate());  
    try {   
        for (int i=1 ; i<=frameGrabber.getLengthInFrames();i++){
            frameGrabber.setFrameNumber(i);
            frame = frameGrabber.grabKeyFrame();  
            Java2DFrameConverter v = new Java2DFrameConverter();
            BufferedImage bi = v.convert(frame);  
            list_BI.add(bi);
         }
       frameGrabber.stop();                                                                        
     } catch (Exception e) {  
       e.printStackTrace(); 
       
     }   System.out.print("***"); System.out.println(list_BI.size());
        for (int i=0;i<list_BI.size();i++){
                 list_I.add(SwingFXUtils.toFXImage(list_BI.get(i), null));
                 ImageView iv = new ImageView();
                 iv.setImage(list_I.get(i));
                 iv.setFitHeight(100);
                 iv.setFitWidth(100);
                 AR_IM.add(iv);
                 CheckBox c= new CheckBox();
                 c.setText(Integer.toString(i));
                 c.setTranslateY(60);
                 check.add(c);
                 StackPane s = new StackPane();
                 s.getChildren().addAll(AR_IM.get(i),check.get(i));
                 stack.add(s);
            }
        for (int i=0 ; i<stack.size();i++){
         root_h.getChildren().add(stack.get(i));
         root_h.setVgap(20);
         root_h.setHgap(20);
        } 
        image.setTranslateX(500);
        delete.setTranslateX(10);
        spane.setTranslateY(5);
        root = new VBox();
        spane.setContent(root_h);
        root_h.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getWidth(), spane.viewportBoundsProperty()));
        root_h.minHeightProperty().bind(Bindings.createDoubleBinding(() -> 
        spane.getViewportBounds().getHeight(), spane.viewportBoundsProperty()));
        root.getChildren().add(navBar);
        root.getChildren().add(image);
        root.getChildren().add(spane);
        Scene scene1 = new Scene(root, 300, 250);
        
        stage1.setTitle("Edit Video");
        stage1.setScene(scene1);
        stage1.show();
            }
        });
        
         
         Button createVideo = new Button();
        createVideo.setText("                      Create Video                               ");
         createVideo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
             public void handle(ActionEvent event) {
            Stage stage2 = new Stage();
            System.out.println("enter video path");
            String path = new Scanner(System.in).next();
            File folder = new File(path);
            ArrayList<IplImage>images=new ArrayList<>();
            File[] listFiles= folder.listFiles();
            System.out.println(listFiles.length);
            if(listFiles.length>0){
               for (int i=0;i<listFiles.length;i++){
                   String files="";
                   if (listFiles[i].isFile()){
                       files=listFiles[i].getName();
                       System.out.println(listFiles[i].getName());
                   }
                  images.add(opencv_imgcodecs.cvLoadImage(path+listFiles[i].getName()));                 
               } 
            }
            FileChooser audio = new FileChooser();
            File f = audio.showOpenDialog(null);
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(f.getPath());
            
                try {
                    //////////////////////////
                    grabber.start();
                } catch (Exception ex) {
                    Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
                }
          FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("C:\\Users\\Lenovo\\Desktop\\"+Math.random()+".mp4",1366,768,grabber.getAudioChannels());
         recorder.setFrameRate(30);
         recorder.setFormat("mp4");
         recorder.setVideoCodec(13);
         recorder.setVideoBitrate(50);
         recorder.setSampleRate(grabber.getSampleRate());
            Frame frame ;
        try {
            recorder.start();
             for (int i=0 ; i<images.size();i++){
                    recorder.record(new OpenCVFrameConverter.ToIplImage().convert(images.get(i))/*,org.bytedeco.javacpp.avutil.AV_PIX_FMT_0RGB32*/);
                    if ((frame=grabber.grabFrame())!=null){
                            recorder.record(frame);
                        }        
             }
           
                try {
                    /*
                    for (int i=0 ; i<images.size();i++){
                    grabber.setFrameNumber(i);
                    recorder.record(grabber.grabKeyFrame());
                    }
                    */
//                    for (int i=0 ; i<images.size();i++){
//                        if ((frame=grabber.grabFrame())!=null){
//                            recorder.record(frame);
//                        }
//                    }
//                    while ((frame= grabber.grabFrame())!= null) {
//                        try {
//                            recorder.record(frame);
//                        } catch (FrameRecorder.Exception ex) {
//                            Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }      
                grabber.stop();
                } catch (Exception ex) {
                    Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
                }
             recorder.stop();
        } catch (FrameRecorder.Exception ex) {
            Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
        }       catch (Exception ex) {   
                    Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
                }
       try {
            if (recorder != null) {
                recorder.release();
            }
            if (grabber != null) {
                grabber.release();
            }
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }       catch (Exception ex) {
                    Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
                }
        
        System.out.println("dove");
//             Scene scene2 = new Scene(root, 300, 250);
//             stage2.setTitle("Edit Video");
//             stage2.setScene(scene2);
//             stage2.show();
                 
             }});
        Button mergeVideos = new Button();
        mergeVideos.setText("                      Merge Videos                               ");
    mergeVideos.setOnAction(new EventHandler<ActionEvent>() {
     @Override
     public void handle(ActionEvent event) {
     String path1,path2,newPath;
    FileChooser fileChooser1 = new FileChooser();
    File file1 = fileChooser1.showOpenDialog(null);   
    path1=file1.getPath();
     FileChooser fileChooser2 = new FileChooser();
    File file2 = fileChooser2.showOpenDialog(null);   
    path2=file2.getPath();
    ArrayList<Image> vid1=new ArrayList<>();
    ArrayList<Image> vid2=new ArrayList<>();
    vid1=cuter(file1);
    vid2=cuter(file2);
    newPath=mix(vid1,vid2);
             }});
        VBox rootB = new VBox();
        EditVideo.setTranslateX(500);
        createVideo.setTranslateX(500);
        mergeVideos.setTranslateX(500);
          EditVideo.setTranslateY(100);
        createVideo.setTranslateY(200);
        mergeVideos.setTranslateY(300);
        rootB.getChildren().add(EditVideo);
        rootB.getChildren().add(createVideo);
        rootB.getChildren().add(mergeVideos);
        Scene scene = new Scene(rootB, 300, 250);
        primaryStage.setTitle("Video Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
        public void save (ArrayList<Image>image,double frameRate){
            int x;
            System.out.println("if you want to set the  width and height of video enter 1");
            x=new Scanner(System.in).nextInt();
            switch (x){
                case 1:
                       System.out.println("enter video width");
                       videoWidth = new Scanner(System.in).nextInt();
                        System.out.println("enter video height");
                       videoHeight = new Scanner(System.in).nextInt();
                       break;
                default:
                    videoWidth=900;
                    videoHeight=1000;
                
            }
         FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("C:\\Users\\Lenovo\\Desktop\\"+Math.random()+".mp4",videoWidth,videoHeight);
         recorder.setFrameRate(frameRate);
         recorder.setFormat("mp4");
         recorder.setFormat("mp4");
         recorder.setSampleRate(frameGrabber.getSampleRate());
         recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
         recorder.setAudioChannels(frameGrabber.getAudioChannels());
        try {
            recorder.start();
             for (int i=0 ; i<image.size();i++){
                    recorder.record(new Java2DFrameConverter().convert(SwingFXUtils.fromFXImage(image.get(i), null)),org.bytedeco.javacpp.avutil.AV_PIX_FMT_0RGB);
        }
             recorder.stop();
        } catch (FrameRecorder.Exception ex) {
            Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
         public static ArrayList<Image> cuter(File  file)
     {
          ArrayList <BufferedImage> list_BI1 = new ArrayList<>();
           ArrayList <Image> list_I1 = new ArrayList<>();
          FFmpegFrameGrabber frameGrabber1 ;
             frameGrabber1 = new FFmpegFrameGrabber(file);
    try {
            frameGrabber1.start();
        } catch (Exception ex) {
            Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
        }
    Frame frame;  
    int frameNo=frameGrabber1.getLengthInFrames();  
    System.out.println(frameNo); 
    System.out.println("Video has "+frameNo+" frames and has frame rate of "+frameGrabber1.getFrameRate());  
    try {   
        for (int i=1 ; i<=/*frameGrabber.getLengthInFrames()*/200;i++){
            frameGrabber1.setFrameNumber(i);
            frame = frameGrabber1.grabKeyFrame();  
            Java2DFrameConverter v = new Java2DFrameConverter();
            BufferedImage bi = v.convert(frame);  
            list_BI1.add(bi);
         }
       frameGrabber1.stop();                                                                        
     } catch (Exception e) {  
       e.printStackTrace(); 
       
     }   System.out.print("***"); System.out.println(list_BI1.size());
        for (int i=0;i<list_BI1.size();i++){
                 list_I1.add(SwingFXUtils.toFXImage(list_BI1.get(i), null));
        }
         return list_I1;
     }
          public static String mix(ArrayList<Image> vid1,ArrayList<Image> vid2)
     {
         String newPath="C:\\Users\\Lenovo\\Desktop\\"+Math.random()+".mp4";
         FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(newPath,900,1000);
         recorder.setFrameRate(30);
         recorder.setFormat("mp4");
         System.out.println(vid1.size());
        try {
            recorder.start();
             for (int i=0 ; i<vid1.size();i++){
                    recorder.record(new Java2DFrameConverter().convert(SwingFXUtils.fromFXImage(vid1.get(i), null)),org.bytedeco.javacpp.avutil.AV_PIX_FMT_0RGB);
        }
               for (int i=0 ; i<vid2.size();i++){
                    recorder.record(new Java2DFrameConverter().convert(SwingFXUtils.fromFXImage(vid2.get(i), null)),org.bytedeco.javacpp.avutil.AV_PIX_FMT_0RGB);
        }
             recorder.stop();
        } catch (FrameRecorder.Exception ex) {
            Logger.getLogger(JavaFXApplication13.class.getName()).log(Level.SEVERE, null, ex);
        }
         System.out.println("done");
         return newPath;
        } 
              private static BufferedImage resize(BufferedImage img, int height, int width) {
        java.awt.Image tmp = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
