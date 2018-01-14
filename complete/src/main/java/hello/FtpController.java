package hello;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FtpController {
	
	@Value("${app.path.base:/}")
	String appBasePath = "";
	String pathDelimiter = File.separator;
	
	@RequestMapping(value = "/filelist", method = RequestMethod.GET)
	public String fileList(@RequestParam(value = "path", required = false, defaultValue = "") String path, 
							@ModelAttribute("currentResourcePath") String currentResourcePath, 
							Model model) {

		System.out.println(path);
		System.out.println("currentResourcePath..." + currentResourcePath);
		
		if(StringUtils.isEmpty(path)) {
			path = appBasePath;
		}
		
		File currentResource = new File(path);

		model.addAttribute("resources", listFilesForFolder(path));
		model.addAttribute("pathDelimiter", pathDelimiter);

		try {
			model.addAttribute("currentpath", currentResource.getCanonicalPath());
		} catch (IOException e) {
			model.addAttribute("currentpath", currentResource.getAbsolutePath());
			e.printStackTrace();
		}

		return "filelist";
	}



	/**
	 * End point which returns any file based on path
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(path = "/filedownload", method = RequestMethod.GET)
	public ResponseEntity<Resource> fileDownload(
			@RequestParam(value = "path", required = false, defaultValue = "/") String filePath) throws IOException {

		File file = new File(filePath);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		headers.add("Content-Disposition", "attachment; filename=" + file.getName());
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}

	/**
	 * Util method to list contents of a folder
	 * 
	 * @param path
	 * @return List<FtpResource>
	 */
	private List<FtpResource> listFilesForFolder(final String path) {
		List<FtpResource> fileList = new ArrayList<>();
		System.out.println(appBasePath);
		File folder = new File(path);
		if (folder.isDirectory()) {
			FtpResource parentFolder = new FtpResource("..", true);
			try {
				if(!folder.getCanonicalPath().equalsIgnoreCase(appBasePath)) {
					
					fileList.add(parentFolder);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (final File fileEntry : folder.listFiles()) {
				FtpResource resource = new FtpResource();
				resource.setName(fileEntry.getName());
				resource.setLastModifiedDate(getFormattedDate(new Date(fileEntry.lastModified()), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
				resource.setIsFolder(fileEntry.isDirectory());
				fileList.add(resource);
				if (fileEntry.isDirectory()) {
					System.out.println("[Folder] \t\t" + resource.getName() + "\t\t" + resource.getLastModifiedDate());
				} else {
					System.out.println("<File> \t\t" + resource.getName() + "\t\t" + resource.getLastModifiedDate());
				}
			}
		}
		return fileList;
	}
	
	/**
	 * Util : Get formatted Date string
	 * 
	 * @param format
	 * @param date
	 * @return String
	 */
	private String getFormattedDate(Date date, String format) {
		
		String formattedDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.US);
		/*if(!StringUtils.isEmpty(timezone)) {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		} else {
			sdf.setTimeZone(TimeZone.getTimeZone(timezone));
		}*/
		formattedDate = sdf.format(date);
		
		return formattedDate;
	}

}
