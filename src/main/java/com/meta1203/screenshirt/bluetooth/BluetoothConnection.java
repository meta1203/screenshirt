package com.meta1203.screenshirt.bluetooth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;

import javax.microedition.io.StreamConnection;

import com.meta1203.screenshirt.ScreenshirtApplication;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BluetoothConnection implements Closeable {
	private boolean closed = false;

	private StreamConnection connection;
	private InputStream is;
	private OutputStream os;

	private BufferedReader reader;
	private BufferedWriter writer;

	public BluetoothConnection(StreamConnection connection) {
		log.info(connection.getClass().getName());
		this.connection = connection;

		try {
			this.is = this.connection.openInputStream();
			Reader r = new InputStreamReader(is);
			this.reader = new BufferedReader(r);

			this.os = this.connection.openOutputStream();
			Writer w = new OutputStreamWriter(os);
			this.writer = new BufferedWriter(w);
		} catch (IOException e) {
			log.error("failed to establish connection", e);
			try {
				this.close();
			} catch (IOException e1) {
				log.error("failed to close", e1);
			}
		}
	}

	public CompletableFuture<String> read() {
		if (this.closed) return CompletableFuture.completedFuture("");

		return CompletableFuture.supplyAsync(() -> {
			StringBuilder sb = new StringBuilder();
			try {
				while (reader.ready()) {
					sb.append(reader.readLine());
					sb.append("\n");
				}
			} catch (IOException e) {
				log.error("failed to read", e);
				try {
					this.close();
				} catch (IOException e1) {
					log.error("failed to close", e1);
				}
			}
			return sb.toString();
		}, ScreenshirtApplication.EXECUTOR);
	}

	public CompletableFuture<Void> write(String... s) {
		if (this.closed) return CompletableFuture.runAsync(() -> {});

		return CompletableFuture.runAsync(() -> {
			try {
				for (String toWrite : s) {
					writer.write(toWrite);
					writer.newLine();
				}
				writer.flush();
			} catch (IOException e) {
				log.error("failed to write", e);
				try {
					this.close();
				} catch (IOException e1) {
					log.error("failed to close", e1);
				}
			}
		}, ScreenshirtApplication.EXECUTOR);
	}

	public CompletableFuture<Void> write(String s) {
		if (this.closed) return CompletableFuture.runAsync(() -> {});

		return CompletableFuture.runAsync(() -> {
			try {
				writer.write(s);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				log.error("failed to write", e);
				try {
					this.close();
				} catch (IOException e1) {
					log.error("failed to close", e1);
				}
			}
		}, ScreenshirtApplication.EXECUTOR);
	}

	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public void close() throws IOException {
		if (this.closed) return;

		reader.close();
		writer.close();
		is.close();
		os.close();

		connection.close();
		this.closed = true;
	}
}
