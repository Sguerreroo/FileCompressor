import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class CompressFile extends SwingWorker<Void, Integer> {
    
    private File directoryFrom, directoryTo;
    private MainWindow window;
    @Override
    protected Void doInBackground() {
        List<String> files = new ArrayList<>();
        getFiles(directoryFrom, files);
        compressFiles(files);
        return null;
    }

    @Override
    protected void process(List<Integer> list) {
        
        System.out.println(this.getProgress());
        window.setProgress(this.getProgress());//To change body of generated methods, choose Tools | Templates.
    }

    protected void setWindow(MainWindow w) {
        this.window = w;
    }
    protected void setDirectoryFrom(File directoryFrom) {
        this.directoryFrom = directoryFrom;
    }

    protected void setDirectoryTo(File directoryTo) {
        this.directoryTo = directoryTo;
    }

    private void getFiles(File directoryFrom, List<String> files) {
        for (File f : directoryFrom.listFiles())
            files.add(f.getPath());
    }

    private void compressFiles(List<String> files) {
        try {
            int BUFFER_SIZE = 1024;
            // Objeto para referenciar a los archivos que queremos comprimir
            BufferedInputStream origin = null;
            // Objeto para referenciar el archivo zip de salida
            FileOutputStream dest = new FileOutputStream(directoryTo.getAbsolutePath() + "\\"+  directoryFrom.getName() + ".zip");
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            // Buffer de transferencia para mandar datos a comprimir
            byte[] data = new byte[BUFFER_SIZE];
            Iterator i = files.iterator();
            int num = 0;
            setProgress(100 * num / files.size());
            publish();
            while(i.hasNext() && !isCancelled()) {
                
                String filename = (String)i.next();
                FileInputStream fi = new FileInputStream(filename);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);

                ZipEntry entry = new ZipEntry( filename );
                out.putNextEntry( entry );
                // Leemos datos desde el archivo origen y los mandamos al archivo destino
                int count;
                while((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                    out.write(data, 0, count);
                }
                // Cerramos el archivo origen, ya enviado a comprimir
                num ++;
                setProgress(100 * num / files.size());
                publish();
                origin.close();
            }
            // Cerramos el archivo zip
            out.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
       
    }
    @Override
    protected void done() {
        if(this.isCancelled()==false){
            JOptionPane.showMessageDialog(null, "Archvo comprimido correctamente", "Information", JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Se ha cancelado", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        
        window.setProgress(0);
        window.reInitialiceCompressFile();
        
    }

  
}
