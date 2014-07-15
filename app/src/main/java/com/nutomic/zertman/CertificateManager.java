package com.nutomic.zertman;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Provides functions to handle system and user certificates using root.
 */
public class CertificateManager {

	private static final String TAG = "CertificateManager";

	public static final File USER_CERTIFICATES_DIR = new File("/data/misc/keychain/cacerts-added");

	public static final File SYSTEM_CERTIFICATES_DIR = new File("/system/etc/security/cacerts");

	/**
	 * Possible options for partition mounting (used for #remountSystem()).
	 */
	private enum Mode {
		ReadOnly,
		ReadWrite
	}

	/**
	 * Lists all certificates for either user or system.
	 *
	 * @param system Whether to list system or user certificates.
	 */
	public List<Certificate> getCertificates(boolean system) {
		String[] list = (system)
				? SYSTEM_CERTIFICATES_DIR.list()
				: USER_CERTIFICATES_DIR.list();
		ArrayList<Certificate> ret = new ArrayList<Certificate>();
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
		remountSystem(Mode.ReadWrite);
		// NOTE: Using mv gives error: "failed on *file* - Cross-device link".
		run("cp " + USER_CERTIFICATES_DIR + "/" + certificate.getFile().getName() +
				" " + SYSTEM_CERTIFICATES_DIR + "/" + certificate.getFile().getName());
		remountSystem(Mode.ReadOnly);
		deleteCertificate(certificate);
		return new Certificate(certificate.getFile().getName(), true);
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

}
