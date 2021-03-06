/**
 * @brief x lib is the library which includes the commonly used functions in 3 Sided Cube Android applications
 *
 * @author Callum Taylor
 **/
package x.lib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

/**
 * @brief This class is used to store and retrive data to the user's phone in a
 *        serialized form
 */
public class CacheManager implements Serializable
{
	private String mCachePath;
	private Context context;
	private String mPackageName;
	private static String mPrefix = "cache_";

	/**
	 * The default constructor
	 *
	 * @param context
	 *            The application's context
	 * @param packageName
	 *            The application's unique package name identifier
	 */
	public CacheManager(Context context, String packageName)
	{
		this(context, packageName, false);
	}

	/**
	 * The default constructor
	 *
	 * @param context
	 *            The context for the applicatiom
	 * @param packageName
	 *            The application's unique package name identifier
	 * @param useExternalCache
	 *            Whether to use the external cache directory or not
	 */
	public CacheManager(Context context, String packageName, boolean useExternalCache)
	{
		this.context = context;
		this.mPackageName = packageName;

		if (useExternalCache)
		{
			if (context.getExternalCacheDir() != null)
			{
				mCachePath = context.getExternalCacheDir().getAbsolutePath();

				return;
			}
		}

		// Not sure if this is the best thing to do by defaulting
		// to internal cache if external cache is available...
		// TODO: change to throw an exception if external cache is
		// not available
		mCachePath = context.getCacheDir().getAbsolutePath();
	}

	/**
	 * The default constructor
	 *
	 * @param path
	 *            The path to the cache directory
	 * @param packageName
	 *            The application's unique package name identifer
	 */
	public CacheManager(String path, String packageName)
	{
		mCachePath = path;
		mPackageName = packageName;
	}

	public String getCachePath()
	{
		return mCachePath;
	}

	/**
	 * Sets the prefix of the files in the cache. Default is "cache_"
	 *
	 * @param prefix
	 *            The new prefix
	 */
	public static void setPrefix(String prefix)
	{
		mPrefix = prefix;
	}

	/**
	 * Gets a base64'd MD5 hash of an input string
	 *
	 * @param input
	 *            The input string
	 * @return The base64 MD5 hash of the input string
	 */
	public static String getHash(String input)
	{
		String hashFileName = "";

		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			hashFileName = Base64.encodeBytes((md5.digest(input.getBytes()))).replace('/', '.');
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return hashFileName;
	}

	/**
	 * Gets a base64'd MD5 hash of an input string
	 *
	 * @param input
	 *            The serializable input data
	 * @return The base64'd MD5 hash of the input string
	 */
	public static String getHash(Serializable input)
	{
		String hashFileName = "";

		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(input);
			byte[] yourBytes = bos.toByteArray();

			out.close();
			bos.close();

			MessageDigest md5 = MessageDigest.getInstance("MD5");
			hashFileName = Base64.encodeBytes((md5.digest(yourBytes))).replace('/', '.');
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return hashFileName;
	}

	public String[] list()
	{
		return list("");
	}

	public String[] list(String folderName)
	{
		File f = new File(mCachePath + "/" + mPrefix + folderName);

		if (f != null)
		{
			String files[] = f.list();

			for (int index = 0; index < files.length; index++)
			{
				files[index] = mCachePath + "/" + (TextUtils.isEmpty(folderName) ? "" : folderName + "/") + files[index];
			}

			return files;
		}

		return null;
	}

	/**
	 * Gets the total size of the cache in bytes
	 *
	 * @return The size of the cache in bytes
	 */
	public long getCacheSize()
	{
		File files = new File(mCachePath);

		FileFilter filter = new FileFilter()
		{
			public boolean accept(File arg0)
			{
				if (arg0.getName().contains(mPrefix))
				{
					return true;
				}

				return false;
			};
		};

		File[] fileList = files.listFiles(filter);

		long totalSize = 0;
		for (File f : fileList)
		{
			totalSize += f.length();
		}

		return totalSize;
	}

	/**
	 * Checks if a file exists within the cache
	 *
	 * @param fileName
	 *            The file to check
	 * @return True if the file exists, false if not
	 */
	public boolean fileExists(String fileName)
	{
		return fileExists(null, fileName);
	}

	/**
	 * Checks if a file exists within the cache
	 *
	 * @param folderName
	 *            The folder path to check
	 * @param fileName
	 *            The file to check
	 * @return True if the file exists, false if not
	 */
	public boolean fileExists(String folderName, String fileName)
	{
		try
		{
			String filePath = mCachePath;
			if (!TextUtils.isEmpty(folderName))
			{
				filePath += "/" + mPrefix + folderName + "/";
			}

			File f = new File(filePath, mPrefix + fileName);

			return f.exists();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Gets the modified date of the file
	 *
	 * @param fileName
	 *            The file
	 * @return The modified date in ms since 1970 (EPOCH)
	 */
	public long fileModifiedDate(String fileName)
	{
		File f = new File(mCachePath, mPrefix + fileName);

		return f.lastModified();
	}

	/**
	 * Checks if a file was created before a certain date
	 *
	 * @param fileName
	 *            The file to check
	 * @param date
	 *            The date to check against
	 * @return True if the file is older, false if not
	 */
	public boolean fileOlderThan(String fileName, long date)
	{
		long lastDate = fileModifiedDate(mPrefix + fileName);

		if (lastDate > date)
		{
			return false;
		}

		return true;
	}

	/**
	 * Gets the file age in ms
	 *
	 * @param fileName
	 *            The file to check
	 * @return The age of the file in ms
	 */
	public long getFileAge(String fileName)
	{
		return Math.abs(fileModifiedDate(mPrefix + fileName) - System.currentTimeMillis());
	}

	/**
	 * Gets the absolute file path of a file in cache
	 *
	 * @param fileName
	 *            The file name
	 * @return The absolute file path
	 */
	public String getFilePath(String fileName)
	{
		return getFilePath("", mPrefix + fileName);
	}

	/**
	 * Gets the absolute file path of a file in cache
	 *
	 * @param folderName
	 *            the folder name
	 * @param fileName
	 *            The file name
	 * @return The absolute file path
	 */
	public String getFilePath(String folderName, String fileName)
	{
		try
		{
			if (folderName != null && !folderName.equals(""))
			{
				folderName = "/" + mPrefix + folderName;
			}

			File file = new File(mCachePath + folderName, mPrefix + fileName);
			return file.getAbsolutePath();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Checks if the cache has reached the user's cache limit stored in user
	 * preference as "cacheLimit" and removes the oldest files to make space
	 */
	public void checkCacheLimit()
	{
		long currentUsed = getCacheSize();
		long currentCacheLimit;

		try
		{
			SharedPreferences mPrefs = context.getSharedPreferences(mPackageName, Context.MODE_WORLD_WRITEABLE);
			currentCacheLimit = mPrefs.getInt("cacheLimit", -1) * 1024 * 1024;
		}
		catch (Exception e)
		{
			currentCacheLimit = -1;
		}

		if (currentCacheLimit > currentUsed || currentCacheLimit < 0)
			return;

		File files = new File(mCachePath);
		FileFilter filter = new FileFilter()
		{
			public boolean accept(File arg0)
			{
				if (arg0.getName().contains(mPrefix))
				{
					return true;
				}

				return false;
			};
		};

		Comparator c = new Comparator<File>()
		{
			public int compare(File object1, File object2)
			{
				if (object1.lastModified() > object2.lastModified())
				{
					return 1;
				}
				else if (object1.lastModified() < object2.lastModified())
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}
		};

		File[] fileList = files.listFiles(filter);
		Arrays.sort(fileList, c);

		for (File f : fileList)
		{
			if (currentUsed > currentCacheLimit)
			{
				currentUsed -= f.length();
				f.delete();
			}
			else
			{
				break;
			}
		}
	}

	/**
	 * Removes an image from the cache
	 *
	 * @param imageName
	 *            The image to remove
	 * @return true if the file was deleted, otherwise false
	 */
	public boolean removeImage(String imageName)
	{
		return removeFile(null, imageName);
	}

	/**
	 * Removes an image from the cache
	 *
	 * @param folderName
	 *            The folder where the image is stored
	 * @param imageName
	 *            The image to remove
	 * @return true if the file was deleted, otherwise false
	 */
	public boolean removeImage(String folderName, String imageName)
	{
		return removeFile(folderName, imageName);
	}

	/**
	 * Removes a file from the cache
	 *
	 * @param fileName
	 *            The file to remove
	 * @return true if the file was deleted, otherwise false
	 */
	public boolean removeFile(String fileName)
	{
		return removeFile("", fileName);
	}

	/**
	 * Removes a file from the cache
	 *
	 * @param folderName
	 *            The folder where the image is stored
	 * @param fileName
	 *            The file to remove
	 * @return true if the file was deleted, otherwise false
	 */
	public boolean removeFile(String folderName, String fileName)
	{
		if (!folderName.equals(""))
		{
			folderName = "/" + mPrefix + folderName;
		}

		File f = new File(mCachePath + folderName, mPrefix + fileName);
		return f.delete();
	}

	/**
	 * Deletes a folder
	 *
	 * @param folderName
	 *            The folder to delete
	 * @return true if the folder was deleted, false if not
	 */
	public boolean removeFolder(String folderName)
	{
		File f = new File(mCachePath + "/" + mPrefix + folderName + "/");

		File[] fileList = f.listFiles();
		double totalSize = 0;
		for (File file : fileList)
		{
			file.delete();
		}

		return f.delete();
	}

	/**
	 * Adds an image to the cache
	 *
	 * @param fileName
	 *            The file name for the file
	 * @param fileContents
	 *            The contents for the file
	 * @return true
	 */
	public boolean addImage(String fileName, Bitmap fileContents)
	{
		return addImage(null, fileName, fileContents, Bitmap.CompressFormat.PNG, null);
	}

	/**
	 * Adds an image to the cache
	 *
	 * @param fileName
	 *            The file name for the file
	 * @param fileContents
	 *            The contents for the file
	 * @param l
	 *            The on file written listener, called after the file was
	 *            written to cache
	 * @return true
	 */
	public boolean addImage(String fileName, Bitmap fileContents, OnFileWrittenListener l)
	{
		return addImage(null, fileName, fileContents, Bitmap.CompressFormat.PNG, l);
	}

	/**
	 * Adds an image to the cache
	 *
	 * @param fileName
	 *            The file name for the file
	 * @param fileContents
	 *            The contents for the file
	 * @param l
	 *            The on file written listener, called after the file was
	 *            written to cache
	 * @return true
	 */
	public boolean addImage(String folderName, String fileName, Bitmap fileContents, OnFileWrittenListener l)
	{
		return addImage(folderName, fileName, fileContents, Bitmap.CompressFormat.PNG, l);
	}

	/**
	 * Adds an image to the cache
	 *
	 * @param fileName
	 *            The file name for the file
	 * @param fileContents
	 *            The contents for the file
	 * @param format
	 *            The compression format for the image
	 * @param l
	 *            The on file written listener, called after the file was
	 *            written to cache
	 * @return true
	 */
	public boolean addImage(String folderName, String fileName, Bitmap fileContents, Bitmap.CompressFormat format, OnFileWrittenListener l)
	{
		AddImageRunnable r = new AddImageRunnable(folderName, fileName, fileContents, format, l)
		{
			public void run()
			{
				try
				{
					File outputPath;
					if (mFolderName != null)
					{
						File f = new File(mCachePath + "/" + mPrefix + mFolderName);
						f.mkdir();

						outputPath = new File(mCachePath + "/" + mPrefix + mFolderName, mPrefix + mFileName);
					}
					else
					{
						outputPath = new File(mCachePath, mPrefix + mFileName);
					}

					FileOutputStream output = new FileOutputStream(outputPath);
					mImage.compress(mFormat, 40, output);

					output.flush();
					output.close();

					if (mListener != null)
					{
						mListener.onFileWritten(mFileName);
					}

					// mImage.recycle();

					// Now delete to make up for more room
					checkCacheLimit();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					checkCacheLimit();
				}
			}
		};

		((Activity)context).runOnUiThread(r);

		return true;
	}

	/**
	 * Adds a file to the cache
	 *
	 * @param fileName
	 *            The file name for the file
	 * @param fileContents
	 *            The contents for the file
	 * @return true
	 */
	public boolean addFile(String fileName, Serializable fileContents)
	{
		return addFile(null, fileName, fileContents, null);
	}

	/**
	 * Adds a file to the cache
	 *
	 * @param folderName
	 *            The folder for the file to be stored in
	 * @param fileName
	 *            The file name for the file
	 * @param fileContents
	 *            The contents for the file
	 * @return true
	 */
	public boolean addFile(String folderName, String fileName, Serializable fileContents)
	{
		return addFile(folderName, fileName, fileContents, null);
	}

	/**
	 * Adds a file to the cache
	 *
	 * @param fileName
	 *            The file name for the file
	 * @param fileContents
	 *            The contents for the file
	 * @param l
	 *            The listener for when the file has been written to cache
	 * @return true
	 */
	public boolean addFile(String fileName, Serializable fileContents, OnFileWrittenListener l)
	{
		return addFile(null, fileName, fileContents, l);
	}

	/**
	 * Adds a file to the cache
	 *
	 * @param folderName
	 *            The folder for the file to be stored in
	 * @param fileName
	 *            The file name for the file
	 * @param fileContents
	 *            The contents for the file
	 * @param l
	 *            The listener for when the file has been written to cache
	 * @return true
	 */
	public boolean addFile(String folderName, String fileName, Serializable fileContents, OnFileWrittenListener l)
	{
		AddFileRunnable r = new AddFileRunnable(folderName, fileName, fileContents, l)
		{
			public void run()
			{
				try
				{
					String outputPath;
					if (mFolderName != null && mFolderName.length() > 0)
					{
						File f = new File(mCachePath + "/" + mPrefix + mFolderName);
						f.mkdir();

						outputPath = mPrefix + mFolderName + "/" + mPrefix + mFileName;
					}
					else
					{
						outputPath = mPrefix + mFileName;
					}

					FileOutputStream fos = null;
					try
					{
						if (fos == null)
						{
							File f = new File(mCachePath + "/" + outputPath);
							fos = new FileOutputStream(mCachePath + "/" + outputPath);
						}

						fos.write((byte[])mContents);
						fos.close();
					}
					catch (Exception e)
					{
						if (fos == null)
						{
							File f = new File(mCachePath + "/" + outputPath);
							fos = new FileOutputStream(mCachePath + "/" + outputPath);
						}

						ObjectOutputStream stream = new ObjectOutputStream(fos);

						stream.writeObject(mContents);
						stream.flush();
						fos.flush();
						stream.close();
						fos.close();
					}
					finally
					{
						mContents = null;
						System.gc();
					}

					if (mListener != null)
					{
						mListener.onFileWritten(mFileName);
					}

					// Now delete to make up for more room
					checkCacheLimit();
				}
				catch (Exception e)
				{
					e.printStackTrace();

					checkCacheLimit();
				}
			}
		};

		// This might be a bad idea, keep an eye on it
		try
		{
			((Activity)context).runOnUiThread(r);
		}
		catch (Exception e)
		{
			r.start();
		}

		return true;
	}

	/**
	 * Gets the input stream for a file
	 *
	 * @param fileName
	 *            The file to get the input stream of
	 * @return The inputstream, null if there was a problem
	 */
	public InputStream readStream(String fileName)
	{
		return readStream(null, fileName);
	}

	/**
	 * Gets the input stream for a file
	 *
	 * @param folderName
	 *            The folder of the file
	 * @param fileName
	 *            The file to get the input stream of
	 * @return The inputstream, null if there was a problem
	 */
	public InputStream readStream(String folderName, String fileName)
	{
		try
		{
			if (folderName != null && !folderName.equals(""))
			{
				folderName = mPrefix + folderName;
			}
			else
			{
				folderName = "";
			}

			File file = new File(mCachePath + folderName, mPrefix + fileName);
			return new FileInputStream(file);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Reads an image from cache
	 *
	 * @param fileName
	 *            The image to retrieve
	 * @return The file as a bitmap or null if there was an OutOfMemoryError or
	 *         Exception
	 */
	public Bitmap readImage(String fileName)
	{
		return readImage(null, fileName);
	}

	/**
	 * Reads an image from cache
	 *
	 * @paaram folderName The folder in which the cache file is stored
	 * @param fileName
	 *            The image to retrieve
	 * @return The file as a bitmap or null if there was an OutOfMemoryError or
	 *         Exception
	 */
	public Bitmap readImage(String folderName, String fileName)
	{
		FileInputStream input = null;

		try
		{
			if (!TextUtils.isEmpty(folderName))
			{
				folderName = "/" + mPrefix + folderName;
			}
			else
			{
				folderName = "";
			}

			File file = new File(mCachePath + folderName, mPrefix + fileName);
			input = new FileInputStream(file);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inDither = true;

			Bitmap b = BitmapFactory.decodeStream(input, null, opts);

			input.close();

			return b;
		}
		catch (OutOfMemoryError e)
		{
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			input = null;
		}
	}

	/**
	 * Reads a file from cache
	 *
	 * @param fileName
	 *            The file to retrieve
	 * @return The file as an Object or null if there was an OutOfMemoryError or
	 *         Exception
	 */
	public Object readFile(String fileName)
	{
		return readFile(null, fileName);
	}

	/**
	 * Reads a file from cache
	 *
	 * @param folderName
	 *            The folder in which the cache file is stored
	 * @param fileName
	 *            The file to retrieve
	 * @return The file as an Object or null if there was an OutOfMemoryError or
	 *         Exception
	 */
	public Object readFile(String folderName, String fileName)
	{
		FileInputStream input = null;
		ObjectInputStream stream = null;

		try
		{
			String filePath = mCachePath;
			if (folderName != null && !folderName.equals(""))
			{
				filePath += "/" + mPrefix + folderName + "/";
			}

			File file = new File(filePath, mPrefix + fileName);

			input = new FileInputStream(file);
			stream = new ObjectInputStream(input);

			Object data = stream.readObject();
			stream.close();
			input.close();

			return data;
		}
		catch (OutOfMemoryError e)
		{
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			input = null;
			stream = null;
		}
	}

	/**
	 * Clears the cache
	 */
	public void clearCache()
	{
		clearCache(false);
	}

	/**
	 * Clears the cache
	 *
	 * @param showProgress
	 *            Shows a dialog or not
	 */
	public void clearCache(boolean showProgress)
	{
		ProgressDialog dialog = new ProgressDialog(context);
		if (showProgress)
		{
			dialog.setMessage("Clearing Cache");
			dialog.show();
		}

		File files = new File(getCachePath());
		FileFilter filter = new FileFilter()
		{
			public boolean accept(File arg0)
			{
				if (arg0.getName().contains(mPrefix))
				{
					return true;
				}

				return false;
			};
		};

		File[] fileList = files.listFiles(filter);
		double totalSize = 0;
		for (File f : fileList)
		{
			f.delete();
		}

		if (showProgress)
		{
			dialog.dismiss();
		}
	}

	/**
	 * @brief The class that adds files to the cache in its own thread
	 */
	private class AddImageRunnable extends Thread
	{
		protected String mFolderName;
		protected String mFileName;
		protected Bitmap mImage;
		protected Bitmap.CompressFormat mFormat;
		protected OnFileWrittenListener mListener;

		/**
		 * Default constructor for the class
		 *
		 * @param folderName
		 *            The folder name for the image to be stored
		 * @param fileName
		 *            The file name for the image
		 * @param image
		 *            The image to store
		 * @param format
		 *            The compression format
		 * @param l
		 *            The callback listener for when the file is written
		 */
		public AddImageRunnable(String folderName, String fileName, Bitmap image, Bitmap.CompressFormat format, OnFileWrittenListener l)
		{
			mFolderName = folderName;
			mFileName = fileName;
			mImage = image;
			mFormat = format;
			mListener = l;
		}
	}

	/**
	 * @brief The class that adds files to the cache in its own thread
	 */
	private class AddFileRunnable extends Thread
	{
		protected String mFolderName;
		protected String mFileName;
		protected Serializable mContents;
		protected OnFileWrittenListener mListener;

		/**
		 * Default constructor for the class
		 *
		 * @param folderName
		 *            The folder name for the image to be stored
		 * @param fileName
		 *            The file name for the file
		 * @param contents
		 *            The contents of the file to be written
		 * @param l
		 *            The callback listener for when the file is written
		 */
		public AddFileRunnable(String folderName, String fileName, Serializable contents, OnFileWrittenListener l)
		{
			mFolderName = folderName;
			mFileName = fileName;
			mContents = contents;
			mListener = l;
		}
	}

	/**
	 * @brief Interface for when the file has been written to cache
	 */
	public interface OnFileWrittenListener
	{
		/**
		 * Method called when the file has been written to the cache
		 *
		 * @param fileName
		 *            The file name of the file written
		 */
		public void onFileWritten(String fileName);
	}

	/**
	 * @brief The class that serailizes data
	 */
	public static class Serializer implements Serializable
	{
		/**
		 * Serializes a bitmap into a byte array to be stored as a file
		 *
		 * @param data
		 *            The bitmap to serialize
		 * @return The serialized image
		 */
		public static byte[] serializeBitmap(Bitmap data)
		{
			try
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				data.compress(CompressFormat.PNG, 100, bos);
				byte[] bytes = bos.toByteArray();

				return bytes;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * Serializes data into bytes
		 *
		 * @param data
		 *            The data to be serailized
		 * @return The serialized data in a byte array
		 */
		public static byte[] serializeObject(Object data)
		{
			try
			{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(bos);
				out.writeObject(data);
				byte[] yourBytes = bos.toByteArray();

				return yourBytes;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * Deserailizes data into an object
		 *
		 * @param data
		 *            The byte array to be deserialized
		 * @return The data as an object
		 */
		public static Object desterializeObject(byte[] data)
		{
			try
			{
				ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(data));
				Object objectData = input.readObject();
				input.close();

				return objectData;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
}