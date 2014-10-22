package com.nutomic.zertman;

import android.net.http.SslCertificate;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import eu.chainfire.libsuperuser.Shell;

/**
 * Provides functions to handle system and user certificates using root.
 */
public class CertificateManager {

	private static final String TAG = "CertificateManager";

	private static final File USER_CERTIFICATES_DIR_KITKAT =
            new File("/data/misc/keychain/cacerts-added");

    private static final File USER_CERTIFICATES_DIR_LOLLIPOP =
            new File("/data/misc/user/0/cacerts-added");

	public static final File SYSTEM_CERTIFICATES_DIR = new File("/system/etc/security/cacerts");

	public interface OnCertificateChangedListener {
		public void onCertificateChanged();
	}

	/**
	 * Possible options for partition mounting (used for #remountSystem()).
	 */
	private enum Mode {
		ReadOnly,
		ReadWrite
	}

	/**
	 * Contains all certificates that are currently being moved from user to system storage.
	 */
	private Set<Certificate> mCurrentlyMoving =
			Collections.newSetFromMap(new ConcurrentHashMap<Certificate, Boolean>());

	private OnCertificateChangedListener mOnCertificateChangedListener;

	public void setOnCertificateChangedListener(OnCertificateChangedListener listener) {
		mOnCertificateChangedListener = listener;
	}

	/**
	 * Lists all certificates for either user or system.
	 *
	 * @param system Whether to list system or user certificates.
	 */
	public List<Certificate> getCertificates(boolean system) {
		String[] list = (system)
				? SYSTEM_CERTIFICATES_DIR.list()
				: getUserCertificatesDir().list();
		ArrayList<Certificate> ret = new ArrayList<>();
		for (String file : list) {
			ret.add(new Certificate(file, system));
		}
		return ret;
	}

	/**
	 * Moves the specified certificate from user storage to system storage.
	 *
	 * @return The updated certificate (located in system storage).
	 */
	public Certificate moveCertificateToSystem(Certificate certificate) {
		mCurrentlyMoving.add(certificate);
		if (mOnCertificateChangedListener != null) {
			mOnCertificateChangedListener.onCertificateChanged();
		}
		remountSystem(Mode.ReadWrite);
		// NOTE: Using mv gives error: "failed on *file* - Cross-device link".
		run("cp " + getUserCertificatesDir() + "/" + certificate.getFile().getName() +
				" " + SYSTEM_CERTIFICATES_DIR + "/" + certificate.getFile().getName());
		run("chmod 644 " + SYSTEM_CERTIFICATES_DIR + "/" + certificate.getFile().getName());
		remountSystem(Mode.ReadOnly);
		deleteCertificate(certificate);
		mCurrentlyMoving.remove(certificate);
		Certificate newCert = new Certificate(certificate.getFile().getName(), true);
		if (mOnCertificateChangedListener != null) {
			mOnCertificateChangedListener.onCertificateChanged();
		}
		return newCert;
	}

	/**
	 * Deletes the given certificate from storage.
	 *
	 * @return True on success.
	 */
	public boolean deleteCertificate(Certificate certificate) {
		remountSystem(Mode.ReadWrite);
		boolean success = run("rm " + certificate.getFile().getAbsolutePath());
		remountSystem(Mode.ReadOnly);
		if (mOnCertificateChangedListener != null) {
			mOnCertificateChangedListener.onCertificateChanged();
		}
		return success;
	}

	/**
	 * Remounts the /system partition as read write or read only.
	 */
	private void remountSystem(Mode mode) {
		run((mode == Mode.ReadOnly)
				? "mount -o ro,remount /system"
				: "mount -o rw,remount /system");
	}

	/**
	 * Runs the given command as root and logs any errors.
	 *
	 * @param command The command to execute.
	 * @return True on success.
	 */
	private boolean run(String command) {
		List<String> result = Shell.SU.run(command);
		if (result == null) {
			Log.w(TAG, "Failed to execute root command: " + command);
		}
		return result != null;
	}

	/**
	 * Returns strings for certificate naming, copied from AOSP 4.4.4,
	 * packages/apps/Settings/src/com/android/settings/TrustedCredentialsSettings.java:310.
	 */
	public static Pair<String, String> getDescription(Certificate cert) {
		InputStream is = null;
		X509Certificate cert2;
		try {
			CertificateFactory factory = CertificateFactory.getInstance("X509");
			is = new BufferedInputStream(new FileInputStream(cert.getFile()));
			cert2 = (X509Certificate) factory.generateCertificate(is);
		} catch (IOException | CertificateException e) {
			Log.w(TAG, "Failed to read certificate description");
			return new Pair<>(cert.getFile().getName(), "");
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			}
			catch (IOException e) {
				Log.w(TAG, "Failed to close stream", e);
			}
		}

		String primary;
		String secondary;

		SslCertificate c2 = new SslCertificate(cert2);
		String cn = c2.getIssuedTo().getCName();
		String o = c2.getIssuedTo().getOName();
		String ou = c2.getIssuedTo().getUName();
		if (!o.isEmpty()) {
			if (!cn.isEmpty()) {
				primary = o;
				secondary = cn;
			} else {
				primary = o;
				secondary = ou;
			}
		} else {
			if (!cn.isEmpty()) {
				primary = cn;
				secondary = "";
			} else {
				primary = c2.getIssuedTo().getDName();
				secondary = "";
			}
		}
		return new Pair<>(primary, secondary);
	}

	public boolean isMovingCertificate(Certificate cert) {
		return mCurrentlyMoving.contains(cert);
	}

    /**
     * Returns user certificate directory depending on Android version.
     */
    public static File getUserCertificatesDir() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            return USER_CERTIFICATES_DIR_KITKAT;
        } else {
            return USER_CERTIFICATES_DIR_LOLLIPOP;
        }
    }
}
