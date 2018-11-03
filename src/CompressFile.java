import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
        window.setProgress(this.getProgress());
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
        ZipOutputStream out = null;
        try {
            int BUFFER_SIZE = 1024;
            // Objeto para referenciar a los archivos que queremos comprimir
            BufferedInputStream origin = null;
            // Objeto para referenciar el archivo zip de salida
            FileOutputStream dest = new FileOutputStream(directoryTo.getAbsolutePath() + "\\"+  directoryFrom.getName() + ".zip");
            out = new ZipOutputStream(new BufferedOutputStream(dest));
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
        catch (java.io.FileNotFoundException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "La carpeta que desea comprimir no puede tener subcarpetas.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            this.cancel(true);
            
        }
        catch( Exception e )
        {
            System.out.println("paso por aqui");
            e.printStackTrace();
            
        }
        try{
            if(out != null){
                out.close();
            }
        }catch(Exception e){
            System.out.println("Fichero no se puede cerrar");
        }
        
    }
    @Override
    protected void done() {
        if (!this.isCancelled())
            JOptionPane.showMessageDialog(null, "Archivo comprimido correctamente", "Information", JOptionPane.INFORMATION_MESSAGE);
        else {
            JOptionPane.showMessageDialog(null, "Se ha cancelado", "Information", JOptionPane.INFORMATION_MESSAGE);
            System.out.println(directoryTo.getAbsolutePath() + "\\"+  directoryFrom.getName() + ".zip");
            File zip = new File(directoryTo.getAbsolutePath() + "\\"+  directoryFrom.getName() + ".zip");
            deleteFolder(zip);
            if (zip.exists()) {
                System.out.println("existo");
                if (zip.delete()) {
                    System.out.println("me he borrado");
                } else 
                    System.out.println("no me he borrado");
            } else 
                System.out.println("no existo");
//            deleteFolder(zip);
        }
        window.setProgress(0);
        window.reInitialiceCompressFile();
    }
    
    
    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) {
            System.out.println("Entro en el if");
            for(File f: files)
                if (f.isDirectory()) deleteFolder(f);
                else f.delete();
        } else {
            System.out.println("no entro en el if");
        }
        if (folder.delete()) {
            System.out.println("me borro");
        } else {
            System.out.println("no me borro");
        }
    }

  
}
