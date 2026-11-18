# Prompt A: Interaction (call report) changes, ITF Vault CRM

You are configuring Veeva Vault CRM for ITF Therapeutics. The workspace at `C:\Users\Jay\itf-vault-workspace` is an MDL config pull from the ITF sandbox. Layout: `components\<ComponentType>\<name>\<name>.mdl`; object fields in `Object\<obj>\Field\<field>\`; page layouts are an `.mdl` plus `page_markup.xml`; custom components end in `__c`.

Requirements source (outside the workspace; read-only input, do not copy it into the repo or commit it): `C:\Users\Jay\Claude\itf_migration\context_for_claude\Claude-ITF Configuration Requirements - Sheet1.csv`. This prompt covers ONLY these rows: Resource Tracking (CLM and Posters), Save for Later, and free-text Discussion Topics. Do not touch the survey rows (that is Prompt B).

## Hard rules
1. Edit MDL files in the workspace only. Do NOT deploy and do NOT write to any Vault. The user pushes to git and deploys themselves.
2. Optional read-only access to the ITF sandbox is allowed to check facts: `https://sb-itftherapeutics-itf-therapeutics-vault-crm-sandbox.veevavault.com`. Use the user's existing session (skill `vault:login`). Never read or write any other Vault.
3. The working tree has uncommitted OneKey-related changes (onekey_* fields, ai_settings, global_account_search_settings, manual_territory_alignment_history, etc.). Do NOT modify, stage or commit them. Run `git status` first and note them.
4. Create a new branch off `main` (for example `feature/interaction-changes`). Commit ONLY files you created or changed, staged by explicit path (never `git add -A` or `git add .`). Do not push.
5. Write the final report to the user when done (see "Report").

## Scope: MSL Meeting Brief only
Object type `call2__v.mslmeetingbrief__v`. Layouts to change: `Pagelayout\itf_msl_meeting_brief__c` and `Pagelayout\msl_meeting_brief_detail_page_layout__c`. Layouts are shared across profiles via `Layoutprofile\<profile>\Objectlayout\call2_mslmeetingbrief__c`. Do not edit other layouts. Confirm the layout binding yourself before editing and tell the user if it differs.

## Task 1: Free-text "Discussion Topics"
- `call2__v` has NO free-text discussion topics field today. A picklist `discussion_topics__c` exists on `call2_discussion__v` (do not change it), and the existing layout section titled "Discussion Topics" is really a key-messages section.
- Create a new long-text field on `call2__v`, label "Discussion Topics" (about 32,000 characters, custom `__c` name, for example `discussion_topics_free_text__c`). Add a Typefield on `mslmeetingbrief__v` if the pattern in this repo requires it, add it to the two page layouts, and follow the repo's field and Typefield MDL conventions.
- Check permission set / security profile field permissions: the MSL profiles (`itf_msl_crm__c`, `itf_msl_manager_crm__c` and any others that use these layouts) need Edit on the field. Update the permission sets or security profiles using the repo's existing pattern.
- Flag the label clash with the existing "Discussion Topics" section title on the layout, and propose a resolution in the report. Do not rename the existing section unless it is unambiguous.

## Task 2: CLM / resource tracking on the call report
- Ask: capture CLM presentations and slides discussed during the interaction, with "CLM details to be enabled on call report", and VMOCs for Windows and Mac.
- Existing assets: `call2_key_message__v` (clm_presentation__v, clm_id__v, slide fields), `clm_presentation__v`, and `call2__v` fields clm__v, presentations__v, remote_clm_*, etc. There is no `call_clm_detail__v`.
- Investigate what is needed for CLM details to display and be stored on the MSL Meeting Brief (page layout section controls, object type and field permissions, Veeva settings). Check the Vault CRM docs through the Vault MCP (`search_documentation`, `app_family=vault_crm`, `release=limited`).
- Apply the Vault-side config in MDL for the MSL Meeting Brief only.
- VMOC: configure only what lives in the Vault (permission sets, security profiles, the `load_vmoc_job__v` job, Veeva settings). Windows and Mac client installs and app settings are OUT OF SCOPE; list them as manual follow-ups.
- Existing VMOC artifacts: `Jobmetadata\load_vmoc_job__v`, `Messagegroup\core__v\Message\vmocs_present__v`, `Permissionset\vmocfield_enable_enhanced_sync_permissio__c`, and VMOC mentions in the itf_frm_crm__c, itf_isr_crm__c and itf_rep_crm__c security profiles. Do not assume the MSL profiles are already covered.

## Task 3: Save for Later
- Requirement: let reps present CLM media outside an active call report, without first selecting an account or creating a call, and store the tracked interaction as an Unassigned Presentation to link to a call report later.
- Nothing exists in the repo (the only "unassigned" hit is the unrelated `veeva_settings__v.auto_add_unassigned__v`).
- FIRST read the Vault CRM docs for this feature. Before editing anything, summarise the exact settings, objects, tabs, permissions and layouts it requires and how it applies to this Vault.
- Then apply it for the MSL profiles only (`itf_msl_crm__c`, `itf_msl_manager_crm__c` and any other profile that uses the MSL Meeting Brief).
- If it needs a Veeva Setting record, a data load, a tab, or a client setting that MDL cannot express, do not guess: list it under "manual follow-ups".

## Report (final message to the user)
1. Files created or changed (explicit list), and the branch and commit hash.
2. For each task: what you did, what you chose and why, and anything you could not do in MDL.
3. Manual follow-ups for the user (VMOC client installs, settings records, data).
4. Open questions or assumptions you made.
5. Remind the user that OneKey changes remain uncommitted in the working tree and need their review.
