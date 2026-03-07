(function() {
    async function addVersionDropdown(menuBar) {
        if (document.querySelector('#mdbook-version-select')) return;

        // Fetch versions.json
        let versions = [];
        try {
            const res = await fetch('../versions.json');
            if (res.ok) {
                versions = await res.json();
            } else {
                console.warn('Could not fetch versions.json:', res.status);
            }
        } catch (err) {
            console.error('Error fetching versions.json:', err);
            return;
        }

        if (!versions.length) return;

        // Create container
        const container = document.createElement('div');
        container.id = 'mdbook-version-container';
        const menuBarHeight = Math.round(menuBar.getBoundingClientRect().height);
        const selectHeight = Math.max(menuBarHeight - 10, 32);
        container.style.cssText = `
            display: flex;
            align-items: center;
            margin-right: auto;
            margin-left: 0.75rem;
            height: ${menuBarHeight}px;
        `;

        // Create select
        const select = document.createElement('select');
        select.id = 'mdbook-version-select';
        select.style.cssText = `
            padding: 0 0.75rem;
            font-size: 1.2rem;
            font-family: inherit;
            padding: 0.3rem 0.5rem;
            border-radius: 5px;
            border: 1px solid #ccc;
            background-color: #0b3954;
            color: white;
            font-weight: bold;
            cursor: pointer;
            height: ${selectHeight}px;
            min-width: 12rem;
            box-sizing: border-box;
            line-height: 1.2;
        `;
        container.appendChild(select);

        // Populate options
        versions.forEach(v => {
            const opt = document.createElement('option');
            opt.value = v.folder.endsWith('/') ? v.folder : v.folder + '/';
            opt.text = v.name;
            select.appendChild(opt);
        });

        // Select current version based on URL
        const path = window.location.pathname;
        for (let i = 0; i < select.options.length; i++) {
            if (path.includes(select.options[i].value)) {
                select.selectedIndex = i;
                break;
            }
        }

        // Navigate on change
    select.addEventListener('change', e => {
        const selectedBranch = e.target.value.replace(/\/$/, ''); // remove trailing slash

        // Current URL path: /meta-tegra/master/pagename
        const parts = window.location.pathname.split('/');

        // Assume: parts[1] = current branch
        const pagePath = parts.slice(2).join('/'); // everything after branch

        // Construct new URL
        const newUrl = `/${selectedBranch}/${pagePath}`;
        window.location.href = newUrl;
    });

        menuBar.prepend(container);

    }

    // Wait until the menu bar exists
    const observer = new MutationObserver((mutations, obs) => {
        const menuBar = document.getElementById('mdbook-menu-bar');
        if (menuBar) {
            addVersionDropdown(menuBar);
            obs.disconnect();
        }
    });

    observer.observe(document.body, { childList: true, subtree: true });
})();
