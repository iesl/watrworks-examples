package examples

import edu.umass.cs.iesl.watr._
import corpora._
// import textboxing.{TextBoxing => TB}, TB._
import textreflow.data._
import TypeTags._

object Examples extends App {
  override def main(args: Array[String]) = {

    val corpus = new SampleTextCorpus()

    // Stable IDs are arbitrary strings, but must be unique within a given corpus,
    //   often just a filename
    val stableId = DocumentID("some-id#23")

    // Load a 2-page document sample w/give id
    corpus.loadSampleDoc(stableId, 2)

    reportDocuments(stableId, corpus.docStore)
  }

  def reportDocuments(stableId: String@@DocumentID, docStore: DocumentCorpus): Unit = {
    for {
      docId <- docStore.getDocument(stableId)
    } {
      println(s"Document ${docId} (${stableId})")

      for {
        pageId <- docStore.getPages(docId)
      } {
        println(s"Page ${pageId}")

        // Every page has a geometry bounding box
        val pageGeometry = docStore.getPageGeometry(pageId)
        println("PageGeometry")
        println(pageGeometry)

        // A TargetRegion is a bounding box for a specific document page:
        val allTargetRegions = docStore.getTargetRegions(pageId)

        println(s"TargetRegion count: ${allTargetRegions.length} ")
        val regionId = docStore.getTargetRegions(pageId).head
        val oneTargetRegion = docStore.getTargetRegion(regionId)

        println(s"First TargetRegion: ${oneTargetRegion}")

      }

      println()
      println("All Document Zones")

      // A Zone is an ordered list of TargetRegions, with one or more labels
      // It may also have text associated with it, in the form of a TextReflow.
      //   (more on TextReflows later)
      //
      // Documents are initially segmented into zones labeled VisualLine,
      //   corresponding to the lines on a page
      for {
        zoneId <- docStore.getZonesForDocument(docId)
        textReflow <- docStore.getTextReflowForZone(zoneId)
      } {
        val zone = docStore.getZone(zoneId)
        val ls = zone.labels
        val zoneTargetRegions = zone.regions
        println("  " + zone)
        println("  " + textReflow.toText)

        // TextReflow is important: here's what it can do:
        // Act much like a normal string:
        val len = textReflow.length
        // Create a normal String
        val text = textReflow.toText()

        // textReflow.slice(begin: Int,  c)
        // Compute the bounding box within which the text appears
        textReflow.targetRegion()
        // Compute all char-level bounding boxes for the text
        textReflow.targetRegions()

        // Examine the individual chars w/bounding boxes
        textReflow.charAtoms().take(3).foreach { charAtom =>
          println(s"${charAtom.char}: ${charAtom.targetRegion}")
        }

        // Sliding trigram example:
        for {
          i <- 0 until textReflow.length
          slice <- textReflow.slice(i, i+3)
        }  {
          println(
            s"""Tri: ${slice.toText()} ${slice.targetRegion().bbox}"""
          )
        }

      }



    }
  }



}
