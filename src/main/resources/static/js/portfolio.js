// 页面加载后初始化作品集
document.addEventListener('DOMContentLoaded', function() {
    // 初始化作品集
    initPortfolio();
});

// 初始化作品集
function initPortfolio() {
    // 从API加载作品集数据
    loadPortfolioProjects();

    // 绑定过滤按钮事件
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            // 切换按钮状态
            document.querySelectorAll('.filter-btn').forEach(b => {
                b.classList.remove('active', 'bg-primary', 'text-white');
                b.classList.add('bg-white', 'text-gray-700');
            });
            this.classList.add('active', 'bg-primary', 'text-white');
            this.classList.remove('bg-white', 'text-gray-700');

            // 过滤项目
            const category = this.getAttribute('data-category') || 'all';
            filterPortfolioProjects(category);
        });
    });
}

// 加载作品集项目
function loadPortfolioProjects() {
    const portfolioGrid = document.getElementById('portfolioGrid');
    portfolioGrid.innerHTML = '<div class="loading col-span-full">加载中...</div>';

    // 模拟从API加载数据（实际项目中替换为真实API请求）
    fetch('/api/portfolio/projects')
        .then(response => response.json())
        .then(projects => {
            // 存储项目数据
            localStorage.setItem('portfolioProjects', JSON.stringify(projects));
            // 显示项目
            displayPortfolioProjects(projects);
        })
        .catch(error => {
            console.error('加载作品集失败:', error);
            portfolioGrid.innerHTML = '<div class="no-articles col-span-full">暂无作品集项目</div>';
        });
}

// 显示作品集项目
function displayPortfolioProjects(projects) {
    const portfolioGrid = document.getElementById('portfolioGrid');
    portfolioGrid.innerHTML = '';

    if (projects.length === 0) {
        portfolioGrid.innerHTML = '<div class="no-articles col-span-full">暂无作品集项目</div>';
        return;
    }

    projects.forEach(project => {
        const card = createPortfolioCard(project);
        portfolioGrid.appendChild(card);
    });
}

// 创建作品集卡片
function createPortfolioCard(project) {
    const card = document.createElement('div');
    card.className = 'portfolio-card';
    card.dataset.category = project.category;

    // 直接使用 JavaScript 拼接路径（移除 Thymeleaf 表达式）
    const imageUrl = `/uploads/portfolio/${project.coverImage}`;

    card.innerHTML = `
        <img src="${imageUrl}" alt="${project.title}" class="w-full h-64 object-cover">
        <div class="project-info">
            <h3>${project.title}</h3>
            <p>${project.description}</p>
            <div class="info-div">
                <span class="publish-date"><i class="far fa-calendar-alt"></i> ${formatDate(project.startDate)}</span>
                <a href="#" class="view-more" data-id="${project.id}">
                    查看详情 <i class="fas fa-arrow-right"></i>
                </a>
            </div>
        </div>
    `;

    // 绑定查看详情事件
    card.querySelector('.view-more').addEventListener('click', function(e) {
        e.preventDefault();
        const projectId = this.getAttribute('data-id');
        showPortfolioDetails(projectId);
    });

    return card;
}

// 过滤作品集项目
function filterPortfolioProjects(category) {
    const projects = JSON.parse(localStorage.getItem('portfolioProjects')) || [];
    const filteredProjects = category === 'all'
        ? projects
        : projects.filter(project => project.category === category);

    displayPortfolioProjects(filteredProjects);
}

// 显示作品集详情
function showPortfolioDetails(projectId) {
    const projects = JSON.parse(localStorage.getItem('portfolioProjects')) || [];
    const project = projects.find(p => p.id == projectId);

    if (!project) {
        alert('项目不存在');
        return;
    }

    // 创建模态框
    const modal = document.createElement('div');
    modal.id = 'portfolioModal';
    modal.className = 'fixed inset-0 bg-black/50 flex items-center justify-center z-50';

    modal.innerHTML = `
        <div id="portfolioModalContent" class="bg-white rounded-lg shadow-xl w-full max-w-4xl m-4 overflow-hidden">
            <button class="close-modal text-2xl text-gray-500 hover:text-gray-700">
                <i class="fas fa-times"></i>
            </button>
            <img src="/uploads/portfolio/${project.coverImage}" alt="${project.title}" class="modal-image">
            <div class="p-6">
                <h2 class="modal-title">${project.title}</h2>
                <div class="modal-meta">
                    <span><i class="far fa-calendar-alt mr-1"></i> ${formatDate(project.startDate)}</span>
                    <span><i class="far fa-clock mr-1"></i> ${project.duration}</span>
                </div>
                <div class="modal-tags">
                    ${project.tags.map(tag => `<span class="tag">${tag}</span>`).join('')}
                </div>
                <div class="modal-content" th:utext="${project.content}">${project.content}</div>
                <div class="flex justify-between mt-6">
                    <a href="${project.projectUrl}" target="_blank" class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md transition-colors">
                        访问项目
                    </a>
                    <a href="${project.githubUrl}" target="_blank" class="bg-gray-800 hover:bg-gray-700 text-white px-4 py-2 rounded-md transition-colors">
                        <i class="fab fa-github mr-1"></i> GitHub
                    </a>
                </div>
            </div>
        </div>
    `;

    document.body.appendChild(modal);

    // 绑定关闭事件
    modal.querySelector('.close-modal').addEventListener('click', function() {
        document.body.removeChild(modal);
    });

    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            document.body.removeChild(modal);
        }
    });
}

// 格式化日期
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' });
}
// 从API加载作品集项目（示例）
fetch('/api/portfolio/projects')
    .then(response => response.json())
    .then(projects => {
        // 处理项目数据
    })
    .catch(error => {
        console.error('加载失败:', error);
    });