  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne">
          #1 [ ~ COMPONENT-NAME ~]
        </a>
      </h4>
    </div>
    <div id="collapseOne" class="panel-collapse collapse in">
      <div class="panel-body">
		<ul class="nav nav-sidebar">
			<li><a href="">Item</a></li>
			
			<!-- ALWAYS use 'request.getContextPath()' when rendering anchor links -->
			<li class="active"><a href="/[ ~ COMPONENT NAME ~ ]/[ ~ MODULE NAME ~ ]">[ ~ MODULE NAME ~ ]</a></li>
			<li><a href="#">A [ ~ MODULE NAME ~ ]</a></li>
			<li><a href="#">B [ ~ MODULE NAME ~ ]</a></li>
			<li><a href="#">C [ ~ MODULE NAME ~ ]</a></li>
		</ul>
      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo">
          #2 [ ~ COMPONENT-NAME ~]
        </a>
      </h4>
    </div>
    <div id="collapseTwo" class="panel-collapse collapse">
      <div class="panel-body">
	       	<ul class="nav nav-sidebar">
				<li><a href="#">A [ ~ MODULE NAME ~ ]</a></li>
				
				<!-- SUB LEVEL MENU EXAMPLE  -->
				<li>
					<div class="panel-group" id="accordion_sub">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" data-parent="#accordion_sub" href="#collapseTest">B [ ~ MODULE NAME ~ ]</a>
								</h4>
							</div>
							<div id="collapseTest" class="panel-collapse collapse">
								<div class="panel-body">
									<ul class="nav nav-sidebar">
										<li><a href="/[ ~ COMPONENT NAME ~ ]/[ ~ MODULE NAME ~ ]">SUB LEVEL - B [ ~ MODULE NAME ~ ]</a></li>
										<li><a href="/[ ~ COMPONENT NAME ~ ]/[ ~ MODULE NAME ~ ]">SUB LEVEL - B [ ~ MODULE NAME ~ ]</a></li>
										<li><a href="/[ ~ COMPONENT NAME ~ ]/[ ~ MODULE NAME ~ ]">SUB LEVEL - B [ ~ MODULE NAME ~ ]</a></li>										
									</ul>
								</div>
							</div>
						</div>
					</div>
				</li>			
			</ul>
      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a data-toggle="collapse" data-parent="#accordion" href="#collapseThree">
          #3 [ ~ COMPONENT-NAME ~]
        </a>
      </h4>
    </div>
    <div id="collapseThree" class="panel-collapse collapse">
      <div class="panel-body">
		<ul class="nav nav-sidebar">
			<li><a href="#">A [ ~ MODULE NAME ~ ]</a></li>
			<li><a href="#">B [ ~ MODULE NAME ~ ]</a></li>
			<li><a href="#">C [ ~ MODULE NAME ~ ]</a></li>
			<li><a href="#">D [ ~ MODULE NAME ~ ]</a></li>
		</ul>
      </div>
    </div>
  </div>