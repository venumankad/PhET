<?php

    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");       
    include_once(SITE_ROOT."admin/db-utils.php");        

    function print_content() {
        global $contribution_id, $referrer;
        
        $level_names   = contribution_get_level_names_for_contribution($contribution_id);
        $subject_names = contribution_get_subject_names_for_contribution($contribution_id);
        $type_names    = contribution_get_type_names_for_contribution($contribution_id);

        $contribution = contribution_get_contribution_by_id($contribution_id);
        
        eval(get_code_to_create_variables_from_array($contribution));
        
        $contribution_date_created = simplify_sql_timestamp($contribution_date_created);
        $contribution_date_updated = simplify_sql_timestamp($contribution_date_updated);        
        
        $contribution_answers_included = $contribution_answers_included == 1 ? "Yes" : "No";
        
        $type_list    = convert_array_to_comma_list($type_names);
        $subject_list = convert_array_to_comma_list($subject_names);
        $level_list   = convert_array_to_comma_list($level_names);
        

        $files_html = contribution_get_files_listing_html($contribution_id);
        
        $download_script = SITE_ROOT."admin/download-archive.php?contribution_id=$contribution_id";
        
        if ($contribution_duration == '') {
            $contribution_duration = 0;
        }
        
        $comments = contribution_get_comments($contribution_id);
        
        $comment_count = count($comments);
        
        $comments_html = '';
        
        foreach($comments as $comment) {
            $comments_html .= '<p class="comment">&quot;<em>';
            $comments_html .= $comment['contribution_comment_text'];
            $comments_html .= '</em>&quot; - '.$comment['contributor_name'];
            $comments_html .= '</p>';
        }
        
        print <<<EOT
        <div id="contributionview">
        
            <h3>Download Files</h3>
        
            $files_html          
            
            Or you may <a href="$download_script">download</a> all files as a compressed archive.  
            
            <h3>Submission Information</h3>
            
            <div class="field">
                <span class="label">Authors</span>
                <span class="label_content">$contribution_authors &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Contact Email</span>
                <span class="label_content">$contribution_contact_email &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">School/Organization</span>
                <span class="label_content">$contribution_authors_organization &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Submitted</span>
                <span class="left_label_content">$contribution_date_created</span>
                
                <span class="right_label">Updated</span>
                <span class="right_label_content">$contribution_date_updated</span>
            </div>            
            
            <h3>Contribution Description</h3>
            
            <div class="field">
                <span class="label">Title</span>
                <span class="label_content">$contribution_title &nbsp;</span>
            </div>            
            
            <div class="field">
                <span class="label">Keywords</span>
                <span class="label_content">$contribution_keywords &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Description</span>
                <span class="label_content">$contribution_desc &nbsp;</span>
            </div>

            <div class="field">
                <span class="label">Level</span>
                <span class="label_content">$level_list &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Type</span>
                <span class="label_content">$type_list &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Subject</span>
                <span class="label_content">$subject_list &nbsp;</span>
            </div>
            
            <div class="field">
                <span class="label">Duration</span>
                <span class="label_content">$contribution_duration minutes</span>
            </div>
            
            <div class="field">
                <span class="label">Answers Included</span>
                <span class="label_content">$contribution_answers_included</span>
            </div>
            
            <div class="field">
                <span class="label">Standards Compliance</span>
                <span class="label_content"> &nbsp;</span>
            </div>
            
            <div class="field">
EOT;

        contribution_print_standards_compliance($contribution_standards_compliance, true);
        
        $php_self = $_SERVER['PHP_SELF'];

        print <<<EOT
            </div>
            
            <div class="field">
                <span class="label">
                    Comments
                </span>
                
                <span class="label_content">
                    <a href="javascript:void;" onclick="$(this).parent().parent().next().toggle(300);">$comment_count comments</a>
                    (<a href="javascript:void;" onclick="$(this).parent().parent().next().next().toggle(300);">add</a>)
                </span>
            </div>
            
            <div class="comments" style="display: none">
                $comments_html
            </div>
            
            <div style="display: none">
                <form method="post" action="add-comment.php" onsubmit="javascript:return false;">
                    <input type="hidden" name="contribution_id" value="$contribution_id" />
                    <input type="hidden" name="referrer"        value="$php_self?contribution_id=$contribution_id&referrer=$referrer" />
                    
                    <div class="field">
                        <span class="label">Name</span>
                        <span class="label_content">
                            <script type="text/javascript">
                                /*<![CDATA[*/
                                
                                function on_email_entered() {
                                    var name_element = document.getElementById('contributor_name_uid');
                                    
                                    var name = name_element.value;
                                    
                                    HTTP.updateElementWithGet('../admin/do-ajax-login.php?contributor_name=' + 
                                        encodeURI(name), null, 'required_login_info_uid');
                                }

                                function on_remind_me() {
                                    var email_element = document.getElementById('contributor_email_uid');

                                    var email = email_element.value;
                                    
                                    var password_element = document.getElementById('ajax_password_comment_uid');

                                    HTTP.updateElementWithGet('../admin/remind-password.php?contributor_email=' + 
                                        encodeURI(email), null, 'ajax_password_comment_uid');
                                }
                                    
                                function on_email_change() {
                                    var email_element = document.getElementById('contributor_email_uid');

                                    var email = email_element.value;

                                    HTTP.updateElementWithGet('../admin/check-email.php?contributor_email=' + 
                                        encodeURI(email), null, 'ajax_email_comment_uid');
                                }
                                
                                function on_password_change() {
                                    var email_element    = document.getElementById('contributor_email_uid');
                                    var password_element = document.getElementById('contributor_password_uid');
                                    
                                    var email    = email_element.value;
                                    var password = password_element.value;

                                    HTTP.updateElementWithGet('../admin/check-password.php?contributor_email=' + 
                                        encodeURI(email) + '&contributor_password=' + password, null, 'ajax_password_comment_uid');
                                }
                                
                                $('#contributor_name_uid').autocomplete('../admin/get-contributor-names.php');
                                
                                /*]]>*/
                            </script>
                            
                            <input type="text" size="25" name="contributor_name" id="contributor_name_uid" onchange="javascript:on_email_entered();"/>
                        </span>
                    </div>
                
                    <div id="required_login_info_uid">
                    
                    </div>                
                
                    <div class="field">
                        <span class="label">Comment</span>
                        <span class="label_content">
                            <textarea name="contribution_comment_text" cols="40" rows="5" ></textarea>
                        </span>
                    </div>
                    
                    <div class="field">
                        <span class="label">&nbsp;</span>
                        <span class="label_content">
                            <input type="button" onclick="javascript:this.form.submit();" value="Add Comment" name="add" />
                        </span>
                    </div>
                    
                </form>
            </div>
            
        </div>
        
        <p><a href="$referrer">back</a></p>
EOT;
    }
    
    $contribution_id = $_REQUEST['contribution_id'];
    
    if (isset($_REQUEST['referrer'])) {
        $referrer = $_REQUEST['referrer'];
    }
    else {
        $referrer = SITE_ROOT.'teacher_ideas/manage-contributions.php';
    }
    
    print_site_page('print_content', 3);

?>