import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class CompressFile extends SwingWorker<Void, Integer> {
    
    private File directoryFrom, directoryTo;

    @Override
    protected Void doInBackground() {
        List<String> files = getFiles(directoryFrom);
        compressFiles(files);
        return null;
    }

    @Override
    protected void process(List<Integer> list) {
        System.out.println("Progress: " + list.get(0) + "%");
        MainWindow.updateProgess(list.get(0));
    }

    @Override
    protected void done() {
        if(!this.isCancelled()){
            JOptionPane.showMessageDialog(
                null,
                "La compresión ha finalizado con éxito",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null,
                    "Se ha cancelado la compresión",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        MainWindow.resetComponents();
    }

    protected void setDirectoryFrom(File directoryFrom) {
        this.directoryFrom = directoryFrom;
    }

    protected void setDirectoryTo(File directoryTo) {
        this.directoryTo = directoryTo;
    }

    private List<String> getFiles(File directoryFrom) {
        List<String> files = new ArrayList<>();
        for (File f : directoryFrom.listFiles())
            files.add(f.getAbsolutePath());
        return files;
    }

    private void compressFiles(List<String> files) {
        try {
            // Objeto para referenciar a los archivos que queremos comprimir
            BufferedInputStream origin = null;
            // Objeto para referenciar el archivo zip de salida
            FileOutputStream dest = new FileOutputStream(directoryTo.getAbsolutePath() +
                    "\\" + directoryFrom.getName() + ".zip");
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            // Buffer de transferencia para mandar datos a comprimir
            final int BUFFER_SIZE = 1024;
            byte[] data = new byte[BUFFER_SIZE];
            
            final int totalFiles = files.size();
            int numberOfCompressedFiles = 0;
            this.setProgress(100 * numberOfCompressedFiles / totalFiles);
            publish(this.getProgress());
            
            Iterator i = files.iterator();
            while(i.hasNext() && !isCancelled()) {
                final String filename = (String)i.next();
                System.out.println(filename);
                FileInputStream fi = new FileInputStream(filename);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);

                ZipEntry entry = new ZipEntry(filename);
                out.putNextEntry(entry);
                // Leemos datos desde el archivo origen y los mandamos al archivo destino
                int count;
                while((count = origin.read(data, 0, BUFFER_SIZE)) != -1)
                    out.write(data, 0, count);
                
                numberOfCompressedFiles++;
                this.setProgress(100 * numberOfCompressedFiles / totalFiles);
                publish(this.getProgress());
                // Cerramos el archivo origen, ya enviado a comprimir
                origin.close();
            }
            // Cerramos el archivo zip
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
