package reader

import org.apache.lucene.codecs.lucene50.{Lucene50CompoundFormat, Lucene50PostingsFormat}
import org.apache.lucene.codecs.lucene62.Lucene62SegmentInfoFormat
import org.apache.lucene.index._
import org.apache.lucene.store.{BufferedChecksumIndexInput, IOContext, SimpleFSDirectory}
import org.apache.lucene.util.StringHelper

object MyReader extends App {
  println(true)
  val file = new java.io.File(this.args(0))
  val path = java.nio.file.Paths.get(this.args(0))
  val directory = new SimpleFSDirectory(path)

  val segmentId = getSegmentId(directory)

  val lucene62SegmentInfoFormat = new Lucene62SegmentInfoFormat()
  val segmentInfo = lucene62SegmentInfoFormat.read(directory, "_1", segmentId, IOContext.READ)

  val compoundFile = new Lucene50CompoundFormat().getCompoundReader(directory, segmentInfo, IOContext.READ)
  val filesList = compoundFile.listAll().toList

  println(filesList)

  val docFilename = filesList.find(_.endsWith(".doc")).get

  val docsFile = compoundFile.openInput(docFilename, IOContext.READ)

  val length = docsFile.length.toInt

  val bytes = (0 until length).toList.map { _ =>
    docsFile.readByte
  }
  println(bytes.map(_.toChar).mkString)

//  val segmentReadState = new SegmentReadState(compoundFile, segmentInfo, new FieldInfos(new Array[FieldInfo](0)), IOContext.READ, "Lucene50_0")
//  val fields = new Lucene50PostingsFormat().fieldsProducer(segmentReadState)
//  println(fields)

  val reader = DirectoryReader.open(directory)

  for (i <- 0 until reader.numDocs()) {
    val document = reader.document(i)
    document.getFields.toArray.toList.foreach {i =>
      i match {
        case field: IndexableField =>
          println(field.name)
          println(document.get(field.name))
      }
    }
  }



  def getSegmentId(directory: SimpleFSDirectory): Array[Byte] = {
    val checksumInput = directory.openChecksumInput("segments_3", IOContext.READ)
    val inputReader = new BufferedChecksumIndexInput(checksumInput)

    val segmentId: Array[Byte] = new Array[Byte](StringHelper.ID_LENGTH)

    val header = inputReader.readInt()
    val codec = inputReader.readString()
    val version = inputReader.readInt()

    inputReader.readBytes(segmentId, 0, segmentId.length)
    println(segmentId)
    segmentId(segmentId.length - 1) = (segmentId(segmentId.length - 1) - 14).toByte
    segmentId
  }
}