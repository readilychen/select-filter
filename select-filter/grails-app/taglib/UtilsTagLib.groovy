import grails.converters.*

class UtilsTagLib {	


	//没有选择值时，会提交空字符串""
	def selectFilter = { attrs, body ->			

		def domain = attrs['domain']  								//指定筛选的domain类
		def list = attrs['from'] 									//存放对象列表 from优先于domain
		def key = attrs['key']										//显示的文字字段
		def val = attrs['val']										//提交的值字段

		def name = attrs['name']									//表单提交变量名
		def value = attrs['value'] != null ? attrs['value'] : "" 	// input读入/提交的值

		def size = attrs['size'] as int								//显示的列表行数
		def keyword = attrs['keyword']  							//domain时 用keyword筛选 显示的文字字段

		//def noSel = attrs['noSelection']  						//目前不支持
		def opval = attrs['optionKey']								//提交的值字段  (兼容g:select标签)
		def opkey = attrs['optionValue']							//显示的文字字段(兼容g:select标签)
		def id = attrs['id']										//id
		
		//def noSelKey = ""											//目前不支持
		//def noSelVal = ""											//目前不支持

		//*********************外部接口结束***************
		def __name = name + new Date().time 						//标示每一个selectFilter标签
		size = size<2 ? 2 : size									//防止单个列表时无法触发onchange事件

		def __id = id ? id : "" + new Date().time

		def dataList = []	

		if(opkey) {
			key = opkey
		}
		if(opval) {
			val = opval
		}

	 	if(!list) {
			if(domain) {
				def c = grailsApplication.getDomainClass(domain).clazz.createCriteria()
				list = c.list {
					if(keyword) {
						ilike(key,"%${keyword}%")
					}				
				}
			}	
		}			

		list.collect {	
			dataList.add(key: it[key], value: it[val])
		}		

		def dataListJson = dataList as JSON

				
        out.println """
	        		<p style='display:inline;'  id='_select_filter_div_${__name}'>
	        			<input id='_select_filter_input_${__name}' value='输入关键字筛选' type='text' >
	     				<input  id='_select_filter_value_${__name}' type='hidden' value='${value}' name='${name}' >
		 				<select id='${__id}'  size='${size}' width='100'> 
	    				</select>
	    			</p> 

        			<script type='text/javascript'>	  
        				( function() {
							var word_input = document.getElementById('_select_filter_input_${__name}');
        					//var select = document.getElementById('_select_filter_select_${__name}');
        					var select = document.getElementById('${__id}');
        					var value_input = document.getElementById('_select_filter_value_${__name}');
        				//	var value_input = document.getElementById('${__id}');        					
        					var div = document.getElementById('_select_filter_div_${__name}');

        					function loadElement(name) {
        						word_input = document.getElementById('_select_filter_input_' + name);
        					 	//var select = document.getElementById('_select_filter_select_${__name}');
        					var select = document.getElementById('${__id}');
        					var value_input = document.getElementById('_select_filter_value_${__name}');
        				//	var value_input = document.getElementById('${__id}');           
        					 	div = document.getElementById('_select_filter_div_' + name);
        					}

		        			function filter(name) {		        				
		        			    loadElement(name);
		        				var dataList = window.__select_filter_dataMap[name];
							    var keyword = word_input.value;
							    var showList = new Array();
							    for (var i=0; i<dataList.length ;i++) {
							    	if(dataList[i].key.indexOf(keyword)!=-1){
							    		showList.push(dataList[i]);
							    	}
							    }						    
							    						    
							   	
							   	select.innerHTML = '';

							   	if(select) {
								    for (var i=0; i<showList.length ;i++) {
								    	var opt=document.createElement('option');
								    	opt.value = showList[i].value ;
								    	opt.innerHTML = showList[i].key ;
								    	if(showList[i].value=='${value}') {
								    		opt.selected = 'selected';
								    	}						
								    	select.appendChild(opt);   
								    }; 
								}
							}

							function loadFilter(name) {
								loadElement(name);

								if(typeof(window.__select_filter_nodeMap) === 'undefined') {
		        					window.__select_filter_dataMap ={};
	        					}

		        				
		        				if(!window.__select_filter_dataMap[name]) {
		        					window.__select_filter_dataMap[name] = eval(${dataListJson});
		        				}

		        				var dataList = window.__select_filter_dataMap[name];	        				
		        				
		        				if(value_input && value_input.value!='') {
			        				for (var i=0; i<dataList.length ;i++) {
			        					if(dataList[i].value == value_input.value){
			        						word_input.value=dataList[i].key;
			        						value_input.value = dataList[i].value;
			        						break;
			        					}
			        				}
		        				}

		        				if(typeof(window.__select_filter_nodeMap) === 'undefined') {
		        					window.__select_filter_nodeMap = {} ;
	        					}      

	        					word_input.onblur = function(){
	        						setValue(name);
	        					}
	        					word_input.onkeyup = function(){
	        						filter(name);
	        					}
	        					word_input.onfocus = function(){
	        						show(name);
	        					}
	        					select.onblur = function(){
	        						hide(name);
	        					}
	        					select.onchange = function(){
	        						slcted(name);
	        					}
		        				hide(name);
							}

							function setValue(name) {
								loadElement(name);	
		        				var dataList = window.__select_filter_dataMap[name];
		        				
		        				if(word_input.value=='') {
		        					value_input.value = '';  //如果在word_input.value==''时 word_input失去焦点  认为用户没有选择 提交''
		        				}

		        				if(value_input.value) {
			        				for (var i=0; i<dataList.length ;i++) {
			        					if(dataList[i].value == value_input.value){
			        						word_input.value=dataList[i].key;
			        						value_input.value = dataList[i].value;
			        						break;
			        					}
			        				}
		        				}			        				 				
							}

							function show(name) {
								loadElement(name);
								var select = window.__select_filter_nodeMap[name];
								if(word_input.value && word_input.value.indexOf('输入关键字筛选')!=-1) {
									word_input.value = '';
								}								
								if(select) {
									select.style.display = 'inline';
									div.removeChild(select);
									div.appendChild(select);
								}
							}

							function hide(name) {	
								loadElement(name);
								window.__select_filter_nodeMap[name] = select;
								select.style.display = 'none';
								div.removeChild(select);
								div.appendChild(select);
								setValue(name);
							}

							function slcted(name) {
								loadElement(name);
								var lastIndex = select.selectedIndex;
								word_input.value = select.options[lastIndex].innerHTML;
								value_input.value = select.value;							
							} 
							 
        					loadFilter('${__name}');	
	        			}) ();	        				
	        		</script>
   			
	        	"""
	}
}
