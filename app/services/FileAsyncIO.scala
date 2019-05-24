package services

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler}
import java.nio.file.Paths
import java.nio.file.StandardOpenOption._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

//https://gist.github.com/tovbinm/f73849aff169d1ebeb97
/**
  * File asynchronous IO wrapper around java.nio.file for scala Futures
  */
object FileAsyncIO {

  /**
    * Read a file
    * @param file file path
    * @param ec execution context
    * @return file contents as bytes
    */
  def read(file: String)(implicit ec: ExecutionContext): Future[Array[Byte]] = {
    val p = Promise[Array[Byte]]()
    try {
      val channel = AsynchronousFileChannel.open(Paths.get(file), READ)
      val buffer = ByteBuffer.allocate(channel.size().toInt)
      channel.read(buffer, 0L, buffer, onComplete(channel, p))
    }
    catch {
      case t: Throwable => p.failure(t)
    }
    p.future
  }

  /**
    * Read a text file
    * @param file file path
    * @param ec execution context
    * @return file contents as string
    */
  def readText(file: String, charsetName: String = "UTF-8")
              (implicit ec: ExecutionContext): Future[String] = read(file).map(new String(_, charsetName))

  /**
    * Write into a file
    * @param file file path
    * @param bytes file contents as bytes
    * @param ec execution context
    * @return unit
    */
  def write(file: String, bytes: Array[Byte])(implicit ec: ExecutionContext): Future[Unit] = {
    val p = Promise[Array[Byte]]()
    try {
      val channel = AsynchronousFileChannel.open(Paths.get(file), CREATE, WRITE)
      val buffer = ByteBuffer.wrap(bytes)
      channel.write(buffer, 0L, buffer, onComplete(channel, p))
    }
    catch {
      case t: Throwable => p.failure(t)
    }
    p.future.map(_ => {})
  }

  /**
    * Write into a text file
    * @param file file path
    * @param s file contents as string
    * @param ec execution context
    * @return unit
    */
  def writeText(file: String, s: String, charsetName: String = "UTF-8")
               (implicit ec: ExecutionContext): Future[Unit] = write(file, s.getBytes(charsetName))

  private def closeSafely(channel: AsynchronousFileChannel) =
    try {
      channel.close()
    } catch {
      case e: IOException =>
    }

  private def onComplete(channel: AsynchronousFileChannel, p: Promise[Array[Byte]]) = {
    new CompletionHandler[Integer, ByteBuffer]() {
      def completed(res: Integer, buffer: ByteBuffer): Unit = {
        p.complete(Try {
          buffer.array()
        })
        closeSafely(channel)
      }

      def failed(t: Throwable, buffer: ByteBuffer): Unit = {
        p.failure(t)
        closeSafely(channel)
      }
    }
  }

}