package api.utils

import com.wix.accord.Validator
import com.wix.accord.combinators.MatchesRegex

import scala.util.matching.Regex

object Patterns {

  def matchNumbersAndLetters: Validator[String] = new MatchesRegex(new Regex("""^[0-9a-zA-Z]+$""").pattern, partialMatchAllowed = true)

  def matchNumber: Validator[String] = new MatchesRegex(new Regex("""^[0-9]+$""").pattern, partialMatchAllowed = true)

  def matchEmail: Validator[String] = new MatchesRegex(new Regex("""\A([^@\s]+)@((?:[-a-z0-9]+\.)+[a-z]{2,})\z""").pattern, partialMatchAllowed = true)

  def matchPhone: Validator[String] = new MatchesRegex(new Regex("""^\+[0-9]{1,3}[0-9]{1,4}[0-9]{8,11}+$""").pattern, partialMatchAllowed = true)

  def matchSingleWord: Validator[String] = new MatchesRegex(new Regex("""^[a-zA-Z]+$""").pattern, partialMatchAllowed = false)

  def matchMultiWord: Validator[String] = new MatchesRegex(new Regex("""^[a-zA-Z ]+$""").pattern, partialMatchAllowed = true)
}
