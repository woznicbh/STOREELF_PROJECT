package com.storeelf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FilenameUtils;

import com.storeelf.util.exception.StoreElfException;

public class MailUtils {
	private static final Logger logger = Logger.getLogger(MailUtils.class
			.getPackage().getName());

	public static void sendMail(String toEmail, String frmEmail,
			String rplyEmail, File attach, String body, String subject,
			boolean errorIfNoAttachment, String mailhost, String mailport)
			throws StoreElfException {
		logger.info("In Send Mail.");
		logger.info("To Email:" + toEmail);
		logger.info("From Email:" + frmEmail);
		logger.info("Reply Email:" + rplyEmail);
		logger.info("Attachment:" + attach);
		logger.info("Body:" + body);
		logger.info("Subject:" + subject);
		logger.info("errorIfNoAttachment:" + errorIfNoAttachment);

		if (StringUtils.isVoid(toEmail)) {
			throw new StoreElfException("Please provide the To-Address list");
		}
		if (StringUtils.isVoid(frmEmail)) {
			throw new StoreElfException("Please provide the From-Email Address");
		}
		if (StringUtils.isVoid(rplyEmail)) {
			throw new StoreElfException(
					"Please provide the Reply-To-Email Address");
		}
		if (StringUtils.isVoid(mailhost)) {
			throw new StoreElfException("Mail Server Host address is mandatory.");
		}
		if (StringUtils.isVoid(mailport)) {
			throw new StoreElfException("Mail Server Port is mandatory.");
		}
		if (StringUtils.isVoid(subject)) {
			logger.fine("Subject Empty");
		}
		if (StringUtils.isVoid(body)) {
			logger.warning("body Empty");
		}
		if (attach == null || !attach.exists()) {
			logger.warning("Attachment Missing!!!");
			if (errorIfNoAttachment) {
				logger.severe("Exiting!!!");
				throw new StoreElfException("No Attachment.");
			}
		}
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", mailhost);
			props.put("mail.smtp.port", mailport);
			Session session = Session.getDefaultInstance(props, null);
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(frmEmail));
			msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toEmail, false));
			msg.setSubject(subject);
			MimeBodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText(body);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			if (attach != null && attach.exists()) {
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(attach.getAbsolutePath());
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(attach.getName());
				multipart.addBodyPart(messageBodyPart);
			}

			// Put parts in message
			msg.setContent(multipart);

			msg.setSentDate(new Date());

			Transport.send(msg);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static void zipAndSendMail(String toEmail, String frmEmail,
			String rplyEmail, File attach, String body, String subject,
			boolean errorIfNoAttachment, String mailhost, String mailport)
			throws StoreElfException {
		String zipfilename = "";
		File zipfile = null;
		if (attach != null && attach.exists()) {
			zipfilename = MailUtils.getFileNameWithoutExtension(attach)
					+ ".zip";
			MailUtils.zipFile(attach, zipfilename);
			zipfile = new File(zipfilename);
		}
		sendMail(toEmail, frmEmail, rplyEmail, zipfile, body, subject,
				errorIfNoAttachment, mailhost, mailport);
		if (zipfile != null && zipfile.exists()) {
			zipfile.delete();
		}
	}

	public static void zipAndSendMail(String toEmail, String frmEmail,
			String rplyEmail, List<File> attach, String body, String subject,
			boolean errorIfNoAttachment, String mailhost, String mailport)
			throws StoreElfException {
		String zipfilename = "";
		File zipfile = null;
		zipfilename = "attachment.zip";
		MailUtils.zipFiles(attach, zipfilename);
		zipfile = new File(zipfilename);
		sendMail(toEmail, frmEmail, rplyEmail, zipfile, body, subject,
				errorIfNoAttachment, mailhost, mailport);
		if (zipfile != null && zipfile.exists()) {
			zipfile.delete();
		}
	}

	public static void zipFile(File file, String zipfilename)
			throws StoreElfException {
		try {
			// create byte buffer
			byte[] buffer = new byte[1024];
			/*
			 * To create a zip file, use
			 * 
			 * ZipOutputStream(OutputStream out)
			 * constructor of ZipOutputStream class.
			 */
			// create object of FileOutputStream
			FileOutputStream fout = new FileOutputStream(zipfilename);
			// create object of ZipOutputStream from FileOutputStream
			ZipOutputStream zout = new ZipOutputStream(fout);
			// create object of FileInputStream for source file
			FileInputStream fin = new FileInputStream(file.getAbsolutePath());
			/*
			 * To begin writing ZipEntry in the zip file, use
			 * 
			 * void putNextEntry(ZipEntry entry)
			 * method of ZipOutputStream class.
			 * 
			 * This method begins writing a new Zip entry to
			 * the zip file and positions the stream to the start
			 * of the entry data.
			 */
			zout.putNextEntry(new ZipEntry(file.getName()));
			/*
			 * After creating entry in the zip file, actually
			 * write the file.
			 */
			int length;
			while ((length = fin.read(buffer)) > 0) {
				zout.write(buffer, 0, length);
			}
			/*
			 * After writing the file to ZipOutputStream, use
			 * 
			 * void closeEntry() method of ZipOutputStream class to
			 * close the current entry and position the stream to
			 * write the next entry.
			 */
			zout.closeEntry();
			// close the InputStream
			fin.close();
			// close the ZipOutputStream
			zout.close();
		} catch (Exception e) {
			throw new StoreElfException(e);
		}
	}

	public static void zipFiles(List<File> files, String zipfilename) {
		try {
			// create byte buffer
			byte[] buffer = new byte[1024];

			/*
			 * To create a zip file, use
			 * 
			 * ZipOutputStream(OutputStream out)
			 * constructor of ZipOutputStream class.
			 */

			// create object of FileOutputStream
			FileOutputStream fout = new FileOutputStream(zipfilename);

			// create object of ZipOutputStream from FileOutputStream
			ZipOutputStream zout = new ZipOutputStream(fout);
			Iterator<File> it = files.iterator();
			while (it.hasNext()) {
				File src = it.next();
				logger.info("Adding " + src.getAbsolutePath());
				// create object of FileInputStream for source file
				FileInputStream fin = new FileInputStream(src);

				/*
				 * To begin writing ZipEntry in the zip file, use
				 * 
				 * void putNextEntry(ZipEntry entry)
				 * method of ZipOutputStream class.
				 * 
				 * This method begins writing a new Zip entry to
				 * the zip file and positions the stream to the start
				 * of the entry data.
				 */

				zout.putNextEntry(new ZipEntry(src.getName()));

				/*
				 * After creating entry in the zip file, actually
				 * write the file.
				 */
				int length;

				while ((length = fin.read(buffer)) > 0) {
					zout.write(buffer, 0, length);
				}

				/*
				 * After writing the file to ZipOutputStream, use
				 * 
				 * void closeEntry() method of ZipOutputStream class to
				 * close the current entry and position the stream to
				 * write the next entry.
				 */

				zout.closeEntry();

				// close the InputStream
				fin.close();

			}

			// close the ZipOutputStream
			zout.close();

			logger.fine("Zip file has been created!");

		} catch (IOException ioe) {
			logger.severe("IOException :" + ioe);
		}

	}

	public static String getFileNameWithoutExtension(File file) {
		String fileNameWithOutExt = "";
		if (file != null) {
			fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
		}
		return fileNameWithOutExt;
	}

}
