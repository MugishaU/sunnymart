import scala.annotation.tailrec

package object util {
  def foldWhilePresent[A, B](list: List[A])(func: A => Option[B]): List[B] = {
    @tailrec
    def loop(list: List[A], acc: Option[List[B]]): Option[List[B]] = list match {
      case Nil => acc
      case head :: tail => func(head) match {
        case Some(value) => loop(tail, acc.map(_ :+ value))
        case None => None
      }
    }
    loop(list, Option(Nil)).getOrElse(Nil)
  }
}
