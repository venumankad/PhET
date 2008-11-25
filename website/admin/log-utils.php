<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");

define('DEFAULT_LOG_FILE_LOCATION', SITE_ROOT.'logs/');

/**
 * Add the message to the specificed log file, create it and the path if it does note exist
 * 
 * Note: An EOL is NOT added to the message.
 *
 * @param string $log_file file name of the log file
 * @param string $msg message to write to the log file (include carriage return if needed)
 * @param string $log_file_location path to where the log file should be found
 * @return boolean true if successful, false otherwise
 */
function log_message($log_file, $msg, $log_file_location = DEFAULT_LOG_FILE_LOCATION) {
    // Get the whole path
    $file_path = trim($log_file_location, '/\\').DIRECTORY_SEPARATOR.$log_file;

    // Open the file
    $fp = fopen($file_path, 'a');
    if (!$fp) {
        return false;
    }

    // Get a file lock
    $startTime = microtime();
    do {
        $canWrite = flock($fp, LOCK_EX);
        // If lock not obtained sleep for 0 - 100 milliseconds, to avoid collision and CPU load
        if (!$canWrite) {
            usleep(round(rand(0, 100)*1000));
        }
    } while ((!$canWrite) && ((microtime() - $startTime) < 1000));

    // File was locked so now we can store information
    if ($canWrite) {
        $bytes_written = fwrite($fp, $msg);
        if ($bytes_written != strlen($msg)) {
            flock($fp, LOCK_UN);
            fclose($fp);
            return false;
        }
    } 

    // Done with the file
    fclose($fp);

    return true;
}

?>