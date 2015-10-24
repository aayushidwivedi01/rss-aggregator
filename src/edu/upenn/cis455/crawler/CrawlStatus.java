package edu.upenn.cis455.crawler;

import java.lang.Thread.State;
import java.util.LinkedList;

public class CrawlStatus extends Thread {
	private static LinkedList<String> queue;
	private static CrawlerThread[] crawlerThreads;
	private static boolean STOP = false;

	public CrawlStatus(LinkedList<String> queue) {
		this.queue = queue;

	}

	public static synchronized void setStop(boolean value) {
		STOP = value;
	}

	public static synchronized boolean signalStop() {
		return STOP;
	}

	public static void setPool(CrawlerThread[] pool) {
		crawlerThreads = pool;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(5000);

				if (!queue.isEmpty())
					continue;

				for (CrawlerThread th : crawlerThreads) {
					if (th.getState() == State.WAITING)
						break;
					if (th.getState() == State.RUNNABLE)
						continue;

				}

				// stop crawling
				STOP = true;

				for (CrawlerThread th : crawlerThreads) {
					if (th.getState() == State.WAITING) {
						th.interrupt();
					}

				}
				System.out.println("Crawler thread stopping");
				return;

			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		}
	}
}
