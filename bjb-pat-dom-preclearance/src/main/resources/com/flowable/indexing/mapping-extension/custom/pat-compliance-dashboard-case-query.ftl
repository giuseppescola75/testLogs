{
"from": ${start!0},
"size": ${size!10},
"query": {
"bool": {
"filter": [
{ "term": { "caseDefinitionKey": "PCR_C001" } }
<#if requesters?has_content && (requesters?size > 0)>,
  { "terms": { "startUserId": [
  <#list requesters as r>"${r?trim}"<#if r_has_next>,</#if></#list>
  ] } }
</#if>
<#if statuses?has_content && (statuses?size > 0)>,
  { "terms": { "businessStatus": [
  <#list statuses as s>"${s?trim}"<#if s_has_next>,</#if></#list>
  ] } }
</#if>
]
<#if notStartUser??>
  ,
  "must_not": [
  {
  "term": {
  "startUserId": "${notStartUser}"
  }
  }
  ]
</#if>
}
}
}
