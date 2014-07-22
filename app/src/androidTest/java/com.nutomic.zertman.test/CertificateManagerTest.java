package com.nutomic.zertman.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.nutomic.zertman.Certificate;
import com.nutomic.zertman.CertificateManager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class CertificateManagerTest extends AndroidTestCase {

	/**
	 * Android (4.4) seems to name certificates like "5ed36f99.0", so using something
	 * different from that scheme should avoid collisions with real certificates.
	 */
	private static final String TEST_CERTIFICATE_NAME = "test_certificate";

	private CertificateManager mCertificateManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mCertificateManager = new CertificateManager();

		assertTrue(Shell.SU.available());
	}

	@SmallTest
	public void testUserCertificates() {
		Certificate cert = copyCertificate(false);
		assertTrue(mCertificateManager.getCertificates(false).contains(cert));
		assertTrue(mCertificateManager.deleteCertificate(cert));
		assertReadOnly();
	}

	@MediumTest
	public void testMoveCertificate() {
		Certificate cert = copyCertificate(false);
		Certificate newCertificate = mCertificateManager.moveCertificateToSystem(cert);
		assertReadOnly();
		assertNotNull(newCertificate);
		assertEquals(newCertificate.getFile().getName(), cert.getFile().getName());
		assertNotSame(newCertificate.getFile(), cert.getFile());
		assertPermissions(newCertificate.getFile(), "-rw-r--r--");
		assertFalse(mCertificateManager.getCertificates(false).contains(cert));
		assertTrue(mCertificateManager.getCertificates(true).contains(newCertificate));
		assertTrue(mCertificateManager.deleteCertificate(newCertificate));
		assertReadOnly();
	}

	/**
	 * Asserts that file has the given permissions.
	 */
	private void assertPermissions(File file, String expectedPermissions) {
		Process process;
		DataOutputStream dos = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			process = Runtime.getRuntime().exec("sh");
			dos = new DataOutputStream(process.getOutputStream());
			isr = new InputStreamReader(process.getInputStream());
			dos.writeBytes("ls -l " + file.getAbsolutePath() + "\n");
			dos.writeBytes("exit\n");
			br = new BufferedReader(isr);
			assertTrue(br.readLine().startsWith(expectedPermissions));
			dos.flush();
		}
		catch (IOException e) {
			fail();
		}
		finally {
			try {
				dos.close();
				br.close();
				isr.close();
			}
			catch (IOException e) {
				fail();
			}
		}
	}

	@SmallTest
	public void testSystemCertificates() {
		Certificate cert = copyCertificate(true);
		assertTrue(mCertificateManager.getCertificates(true).contains(cert));
		assertTrue(mCertificateManager.deleteCertificate(cert));
		assertReadOnly();
	}

	/**
	 * Checks that /system is properly remounted as read only.
	 */
	private void assertReadOnly() {
		List<String> mount = Shell.SU.run("mount | grep system");
		assertTrue(mount.get(0).contains("ro"));
		assertFalse(mount.get(0).contains("rw"));
	}

	/**
	 * Copies test certificate from resources to user or system certificates.
	 *
	 * This should always work, but seems to fail randomly.
	 */
	private Certificate copyCertificate(boolean isSystemCertificate) {
		FileOutputStream tempFileStream = null;
		InputStream resourceStream = null;
		File source = null;
		try {
			resourceStream = getContext().getResources().openRawResource(R.raw.test_certificate);
			source = new File(getContext().getCacheDir(), "zertman-test.tmp");
			source.createNewFile();
			tempFileStream = new FileOutputStream(source);
			byte[] buff = new byte[1024];
			int read;
			while ((read = resourceStream.read(buff)) > 0) {
				tempFileStream.write(buff, 0, read);
			}
			assertNotNull(Shell.SU.run(
					"mv " + source.getAbsolutePath() + " " +
							CertificateManager.USER_CERTIFICATES_DIR + "/" + TEST_CERTIFICATE_NAME));
			// NOTE: We use CertificateManager.moveCertificateToSystem() to avoid
			// implementing system remount again.
			return (isSystemCertificate)
				? mCertificateManager.moveCertificateToSystem(
						new Certificate(TEST_CERTIFICATE_NAME, false))
				: new Certificate(TEST_CERTIFICATE_NAME, isSystemCertificate);
		}
		catch (FileNotFoundException e) {
			fail();
		}
		catch (IOException e) {
			fail();
		}
		finally {
			source.delete();
			try {
				resourceStream.close();
				tempFileStream.close();
			}
			catch (IOException e) {
				fail();
			}
		}
		return null;
	}

}
